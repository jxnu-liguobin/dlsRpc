package io.growing.dlsrpc.consul.discovery

import java.util.concurrent.TimeUnit
import java.util.{Timer, TimerTask, ArrayList => JArrayList, List => JList}

import com.ecwid.consul.v1.health.HealthServicesRequest
import com.ecwid.consul.v1.health.model.HealthService
import com.ecwid.consul.v1.{QueryParams, Response}
import com.google.common.cache._
import com.google.common.collect.Maps
import com.google.inject.Singleton
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.enums.BalancerType
import io.growing.dlsrpc.common.enums.BalancerType.BalancerType
import io.growing.dlsrpc.common.exception.RpcException
import io.growing.dlsrpc.common.metadata.{NormalServiceAddress, ServiceAddress, WeightServiceAddress}
import io.growing.dlsrpc.common.utils.CheckCondition
import io.growing.dlsrpc.common.utils.ImplicitUtils._
import io.growing.dlsrpc.consul.commons.ConsulBuilder
import io.growing.dlsrpc.consul.loadbalancer.{LoadBalancer, RandomLoadBalancer, WeightLoadBalancer}

/**
 * 使用consul的服务发现
 *
 * @author 梦境迷离
 * @version 1.3, 2019-06-08
 */
@Singleton
class ConsulServiceDiscovery(consulAddress: ServiceAddress) extends ServiceDiscovery with LazyLogging {

  private[this] final lazy val consulClient = ConsulBuilder.checkAndBuild(consulAddress)

  private[this] final val loadBalancerMap = Maps.newConcurrentMap[String, Option[LoadBalancer[ServiceAddress]]]()

  private[this] final val wait_time = 3000

  private[this] val serviceRefresh = new Timer

  //TODO 失败处理，下线剔除
  //TODO 使用ScheduledExecutorService重构
  private[this] val refreshTask: TimerTask = new TimerTask {
    override def run(): Unit = {
      if (!loadBalancerMap.isEmpty) {
        try {
          var consulIndex: Long = -1
          loadBalancerMap.forEach((serviceName, _) => {
            val param: QueryParams = new QueryParams(wait_time, consulIndex)
            val request: HealthServicesRequest = HealthServicesRequest.
              newBuilder().setPassing(true).setQueryParams(param).build
            val healthyServices: Response[JList[HealthService]] = consulClient.getHealthServices(serviceName, request)
            consulIndex = healthyServices.getConsulIndex
            logger.info("Consul index for {} is {}", serviceName, consulIndex)
            val healthServices = healthyServices.getValue
            logger.debug("Service addresses of {} is {}", serviceName, healthServices)
            //把查询到的服务构建成LoadBalancer对象
            val serviceLoadBalancer = buildLoadBalancer[RandomLoadBalancer[NormalServiceAddress]](healthServices, BalancerType.WEIGHT)
            logger.info("LoadBalancerMap is refresh success")
            loadBalancerMap.put(serviceName, serviceLoadBalancer)
          })
        } catch {
          case e: Exception => logger.error("Update LoadBalancerMap fail when serviceRefresh, cause by : {}", e.getMessage)
        }
      } else {
        logger.warn("LoadBalancerMap is null, may no any services")
      }
    }
  }

  //TODO 解决多实例问题
  //TODO 应该有统一的Server提供这些，否则客户端过来的时候拿不到数据
  //  private[this] val initLoadBalancerMap = {
  //    try {
  //      for (hasAnnotationClassName <- ClassUtil.getClassListByAnnotation(Configuration.PACKAGE_SERVICE, classOf[RPCService]).iterator()) {
  //        val request: HealthServicesRequest = HealthServicesRequest.newBuilder().setPassing(true).setQueryParams(QueryParams.DEFAULT).build
  //        val healthyServices: JList[HealthService] = consulClient.getHealthServices(hasAnnotationClassName, request).getValue
  //        val loadBalancer: Option[LoadBalancer[ServiceAddress]] = buildLoadBalancer[WeightLoadBalancer[WeightServiceAddress]](healthyServices, BalancerType.WEIGHT)
  //        loadBalancerMap.put(hasAnnotationClassName, loadBalancer)
  //      }
  //      logger.info("Init LoadBalancerMap successfully")
  //    } catch {
  //      case e: Exception =>
  //        logger.error(s"Init LoadBalancerMap failed, cause by : ${e.getCause}")
  //    }
  //  }

  //60秒后开始，每15秒执行一次
  //  initLoadBalancerMap
  serviceRefresh.schedule(refreshTask, 6000, 15000)

  //使用cache对loadBalancerMap封装，加快速度
  //若缓存命中则直接返回，否则查询loadBalancerMap，若命中则直接返回，否则查询consul，若命中则返回，否则抛出无可用服务异常缓存
  //因为定时器不可靠，无法确保loadBalancerMap中没有命中就是真的不存在
  private val serviceCache: LoadingCache[String, Option[LoadBalancer[ServiceAddress]]] = CacheBuilder.newBuilder()
    .refreshAfterWrite(15, TimeUnit.SECONDS).maximumSize(1000).initialCapacity(10)
    .removalListener((removalNotification: RemovalNotification[String, Option[LoadBalancer[ServiceAddress]]]) => {
      logger.info(s"${removalNotification.getKey}:${removalNotification.getValue},remove cause by:${removalNotification.getCause}")
    }).build(new CacheLoader[String, Option[LoadBalancer[ServiceAddress]]] {
    override def load(keyServiceName: String): Option[LoadBalancer[ServiceAddress]] = {
      try {
        val loadBalancer: Option[LoadBalancer[ServiceAddress]] = if (loadBalancerMap.get(keyServiceName) != null) {
          loadBalancerMap.get(keyServiceName)
        } else {
          val request: HealthServicesRequest = HealthServicesRequest.newBuilder().setPassing(true).setQueryParams(QueryParams.DEFAULT).build
          val healthyServices: JList[HealthService] = consulClient.getHealthServices(keyServiceName, request).getValue
          logger.info(s"LoadBalancerMap'Cache load keyServiceName:$keyServiceName successfully")
          buildLoadBalancer[WeightLoadBalancer[WeightServiceAddress]](healthyServices, BalancerType.WEIGHT)
        }
        loadBalancerMap.put(keyServiceName, loadBalancer)
        loadBalancer
      }
      catch {
        case e: Exception =>
          logger.error(s"Load key:$keyServiceName cause exception:${e.getMessage}")
          throw RpcException("Can't find any available services")
      }
    }
  })

  @volatile def addUpdateKey(key: String, value: LoadBalancer[ServiceAddress]): Unit = {
    serviceCache.put(key, Option(value))
  }

  @volatile def get(key: String): Option[LoadBalancer[ServiceAddress]] = {
    serviceCache.get(key)
  }

  //传进来的是service的类名
  override def discover(serviceName: String): ServiceAddress = {
    CheckCondition.conditionException(serviceName == null, "service name can't be null")
    val sd: ServiceAddress = serviceCache.get(serviceName) match {
      case Some(lbp) => lbp.next
      case None => throw RpcException("Can't find any available services")
    }
    //返回真实服务的地址[ip:port]
    //val sd = loadBalancerMap.get(serviceName).next("127.0.0.1") //加权后hash 请求ip
    //val sd = loadBalancerMap.get(serviceName).next
    logger.info("Real address is {}", sd)
    sd
  }

  /**
   * 构造包含负载均衡器的服务所有的可用列表
   *
   * @param healthServices 可用服务列表
   * @param balancerType   启用的负载均衡类型
   * @tparam L 预期类型 协变
   * @return 实际类型 类型提升
   */
  @volatile private[this] def buildLoadBalancer[L <: LoadBalancer[_]](healthServices: JList[HealthService],
                                                                      balancerType: BalancerType): Option[LoadBalancer[ServiceAddress]] = {
    val address: JList[ServiceAddress] = new JArrayList[ServiceAddress]()
    balancerType match {
      case BalancerType.RANDOM => {
        for (service <- healthServices.iterator()) {
          address.add(NormalServiceAddress(service.getService.getAddress, service.getService.getPort))
        }
        Option(new RandomLoadBalancer(address))
      }
      case BalancerType.WEIGHT => {
        for (service <- healthServices.iterator()) {
          address.add(new WeightServiceAddress(service.getService.getAddress, service.getService.getPort))
        }
        Option(new WeightLoadBalancer[WeightServiceAddress](address.asInstanceOf[JList[WeightServiceAddress]]))
      }
    }
  }
}
