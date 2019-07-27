package io.github.dlsrpc.consul.discovery

import java.util.concurrent.TimeUnit
import java.util.{ArrayList => JArrayList, HashMap => JHashMap, List => JList}
import io.github.dlsrpc.common.utils.ImplicitUtils._
import com.ecwid.consul.v1.QueryParams
import com.ecwid.consul.v1.health.HealthServicesRequest
import com.ecwid.consul.v1.health.model.HealthService
import com.google.common.cache._
import com.typesafe.scalalogging.LazyLogging
import io.github.dlsrpc.common.config.Configuration
import io.github.dlsrpc.common.enums.BalancerType
import io.github.dlsrpc.common.exception.RpcException
import io.github.dlsrpc.common.metadata.{ServiceAddress, WeightServiceAddress}
import io.github.dlsrpc.common.utils.CheckCondition
import io.github.dlsrpc.consul.commons.ConsulBuilder
import io.github.dlsrpc.consul.loadbalancer.{LoadBalancer, WeightLoadBalancer}

/**
 * 使用consul的服务发现
 *
 * @author 梦境迷离
 * @version 1.4, 2019-06-08
 */
class ConsulServiceDiscovery extends ServiceDiscovery with LazyLogging {

  private final lazy val consulClient = ConsulBuilder.checkAndBuild

  private final val cache_refresh_time = 60
  private final val cache_maximum_size = 200
  private final val cache_initial_capacity = 50

  //TODO 启动server时也会初始化一次。 因为服务端和客户端使用了同一个注入模块
  private final lazy val initLoadBalancerMap = () => {
    try {
      val servers = consulClient.getAgentServices.getValue
      for ((_, service) <- servers.iterator) {
        if (ConsulBuilder.loadBalancerMapContext.containsKey(service.getService)) {
          //获取存在的LoadBalancer
          val currentLoadBalancer: Option[LoadBalancer[ServiceAddress]] = ConsulBuilder.loadBalancerMapContext.get(service.getService)
          val weightServiceAddressMap = new JHashMap[WeightServiceAddress, Int]()
          //使用当前地址构造出一个Maps
          weightServiceAddressMap.put(new WeightServiceAddress(service.getAddress, service.getPort), Configuration.DEFAULT_WEIGHT)
          //合并maps
          val res: LoadBalancer[ServiceAddress] = currentLoadBalancer.getOrElse(throw RpcException("not found loadBalancer")) mergeMaps weightServiceAddressMap
          ConsulBuilder.loadBalancerMapContext.put(service.getService, Option(res))
        } else {
          val weightServiceAddress = new JArrayList[WeightServiceAddress]()
          weightServiceAddress.add(new WeightServiceAddress(service.getAddress, service.getPort))
          val res = new WeightLoadBalancer[WeightServiceAddress](weightServiceAddress)
          ConsulBuilder.loadBalancerMapContext.put(service.getService, Option(res))
        }
      }
      logger.info(s"Init LoadBalancerMap successfully, size : {${ConsulBuilder.loadBalancerMapContext.size()}}")
    } catch {
      case e: Exception =>
        logger.error(s"Init LoadBalancerMap failed, cause by : ${e.getMessage}")
        System.exit(-1)
    }
    ConsulBuilder.execSchedulesTask(consulClient)
  }
  initLoadBalancerMap()

  //使用cache对loadBalancerMap封装，加快速度
  //若缓存命中则直接返回，否则查询loadBalancerMap，若命中则直接返回，否则查询consul，若命中则返回，否则抛出无可用服务异常缓存
  //因为定时器不可靠，无法确保loadBalancerMap中没有命中就是真的不存在
  private val serviceCache: LoadingCache[String, Option[LoadBalancer[ServiceAddress]]] = CacheBuilder.newBuilder()
    .refreshAfterWrite(cache_refresh_time, TimeUnit.SECONDS).maximumSize(cache_maximum_size).initialCapacity(cache_initial_capacity)
    .removalListener((removalNotification: RemovalNotification[String, Option[LoadBalancer[ServiceAddress]]]) => {
      logger.info(s"${removalNotification.getKey}:${removalNotification.getValue}, remove cause by:${removalNotification.getCause}")
    }).build(new CacheLoader[String, Option[LoadBalancer[ServiceAddress]]] {
    override def load(keyServiceName: String): Option[LoadBalancer[ServiceAddress]] = {
      try {
        val loadBalancer: Option[LoadBalancer[ServiceAddress]] = if (ConsulBuilder.loadBalancerMapContext.get(keyServiceName) != null) {
          ConsulBuilder.loadBalancerMapContext.get(keyServiceName)
        } else {
          val request: HealthServicesRequest = HealthServicesRequest.newBuilder().setPassing(true).setQueryParams(QueryParams.DEFAULT).build
          val healthyServices: JList[HealthService] = consulClient.getHealthServices(keyServiceName, request).getValue
          logger.info(s"LoadBalancerMap'Cache load keyServiceName:{$keyServiceName} successfully")
          ConsulBuilder.buildLoadBalancer[WeightLoadBalancer[WeightServiceAddress]](healthyServices, BalancerType.WEIGHT)
        }
        ConsulBuilder.loadBalancerMapContext.put(keyServiceName, loadBalancer)
        //        ConsulBuilder.execSchedulesTask(keyServiceName, consulClient) //对该服务进行死循环监听
        loadBalancer
      }
      catch {
        case e: Exception =>
          logger.error(s"Load key:{$keyServiceName} cause exception:{${e.getMessage}}")
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
}
