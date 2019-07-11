package io.growing.dlsrpc.consul.discovery

import java.util.{Timer, TimerTask, ArrayList => JArrayList, List => JList}

import com.ecwid.consul.v1.health.HealthServicesRequest
import com.ecwid.consul.v1.health.model.HealthService
import com.ecwid.consul.v1.{QueryParams, Response}
import com.google.common.collect.Maps
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.enums.BalancerType
import io.growing.dlsrpc.common.enums.BalancerType.BalancerType
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
class ConsulServiceDiscovery(consulAddress: ServiceAddress) extends ServiceDiscovery with LazyLogging {

  private[this] final lazy val consulClient = ConsulBuilder.checkAndBuild(consulAddress)

  private[this] final val loadBalancerMap = Maps.newConcurrentMap[String, LoadBalancer[ServiceAddress]]()

  private[this] final val wait_time = 3000

  private[this] val serviceRefresh = new Timer

  //TODO 失败处理，下线剔除
  private[this] val refreshTask: TimerTask = new TimerTask {
    override def run(): Unit = {
      if (!loadBalancerMap.isEmpty) {
        try {
          loadBalancerMap.forEach((serviceName, _) => {
            //对map中的所有服务进行更新
            var consulIndex: Long = -1
            //超时时间3秒
            val param: QueryParams = new QueryParams(wait_time, consulIndex)
            val request: HealthServicesRequest = HealthServicesRequest.
              newBuilder().setPassing(true).setQueryParams(param).build
            val healthyServices: Response[JList[HealthService]] = consulClient.getHealthServices(serviceName, request)
            consulIndex = healthyServices.getConsulIndex
            logger.debug("Consul index for {} is {}", serviceName, consulIndex)
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

  //60秒后开始，每90秒执行一次
  serviceRefresh.schedule(refreshTask, 60000, 15000)

  //TODO 使用cache对loadBalancerMap封装，加快速度
  //TODO 若缓存中没有，则查询consul，并更新loadBalancerMap，和缓存（必须保证两者是原子操作，最大允许15内数据是过期的）。

  //传进来的是service的类名
  override def discover(serviceName: String): ServiceAddress = {
    CheckCondition.conditionException(serviceName == null, "service name can't be null")
    if (!loadBalancerMap.containsKey(serviceName)) {
      //TODO因为本地没有使用HTTP，无法提供健康检查接口，或者把注册的检查端口改为8500（滑稽）
      val request: HealthServicesRequest = HealthServicesRequest.
        newBuilder().setPassing(true).setQueryParams(QueryParams.DEFAULT).build
      val healthyServices: JList[HealthService] = consulClient.getHealthServices(serviceName, request).getValue
      loadBalancerMap.put(serviceName, buildLoadBalancer[WeightLoadBalancer[WeightServiceAddress]](healthyServices, BalancerType.WEIGHT))
      // 监测 consul
      longPolling(serviceName)
    }
    //返回真实服务的地址（ip:port）可能是null
    //val sd = loadBalancerMap.get(serviceName).next("127.0.0.1") //加权后hash 请求ip
    val sd = loadBalancerMap.get(serviceName).next
    CheckCondition.conditionException(sd.getPort < 0, "port can't less  0")
    logger.info("Real address is {}", sd)
    sd
  }

  //TODO 去掉轮询
  private[this] def longPolling(serviceName: String) {
    new Thread(() => {
      var consulIndex: Long = -1
      do {
        //超时时间3秒
        val param: QueryParams = new QueryParams(wait_time, consulIndex)
        val request: HealthServicesRequest = HealthServicesRequest.
          newBuilder().setPassing(true).setQueryParams(param).build
        val healthyServices: Response[JList[HealthService]] = consulClient.getHealthServices(serviceName, request)
        consulIndex = healthyServices.getConsulIndex
        logger.debug("Consul index for {} is {}", serviceName, consulIndex)
        val healthServices = healthyServices.getValue
        logger.debug("Service addresses of {} is {}", serviceName, healthServices)
        loadBalancerMap.put(serviceName, buildLoadBalancer[RandomLoadBalancer[NormalServiceAddress]](healthServices, BalancerType.WEIGHT))
      } while (true)
    }).start()
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
                                                                      balancerType: BalancerType): LoadBalancer[ServiceAddress] = {
    val address: JList[ServiceAddress] = new JArrayList[ServiceAddress]()
    balancerType match {
      case BalancerType.RANDOM => {
        for (service <- healthServices.iterator()) {
          address.add(NormalServiceAddress(service.getService.getAddress, service.getService.getPort))
        }
        new RandomLoadBalancer(address)
      }
      case BalancerType.WEIGHT => {
        for (service <- healthServices.iterator()) {
          address.add(new WeightServiceAddress(service.getService.getAddress, service.getService.getPort))
        }
        new WeightLoadBalancer[WeightServiceAddress](address.asInstanceOf[JList[WeightServiceAddress]])
      }
    }
  }
}
