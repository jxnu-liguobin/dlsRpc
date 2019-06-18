package io.growing.dlsrpc.consul.discovery

import java.util.{ArrayList => JArrayList, List => JList}

import com.ecwid.consul.v1.health.model.HealthService
import com.ecwid.consul.v1.{QueryParams, Response}
import com.google.common.collect.Maps
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.enums.BalancerType
import io.growing.dlsrpc.common.enums.BalancerType.BalancerType
import io.growing.dlsrpc.common.metadata.{NormalServiceAddress, ServiceAddress, WeightServiceAddress}
import io.growing.dlsrpc.common.utils.ImplicitUtils._
import io.growing.dlsrpc.common.utils.IsCondition
import io.growing.dlsrpc.consul.commons.ConsulBuilder
import io.growing.dlsrpc.consul.loadbalancer.{Loadbalancer, RandomLoadBalancer, WeightLoadBalancer}

import scala.util.Try

/**
 * 使用consul的服务发现
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-08
 */
class ConsulServiceDiscovery(consulAddress: ServiceAddress) extends ServiceDiscovery with LazyLogging {

  private[this] final lazy val consulClient = ConsulBuilder.checkAndBuild(consulAddress)

  private[this] final val loadBalancerMap = Maps.newConcurrentMap[String, Loadbalancer[ServiceAddress]]()

  //传进来的是service的类名
  override def discover(serviceName: String): ServiceAddress = {
    IsCondition.conditionException(serviceName == null, "service name can't be null")
    if (!loadBalancerMap.containsKey(serviceName)) {
      //TODO 优化过期接口，为了测试设置为false，因为本地没有使用HTTP，无法提供健康检查接口，或者把注册的检查端口改为8500（滑稽）
      val healthServices: JList[HealthService] = consulClient.getHealthServices(serviceName,
        true, QueryParams.DEFAULT).getValue
      loadBalancerMap.put(serviceName, buildLoadBalancer[RandomLoadBalancer[ServiceAddress]](healthServices, BalancerType.WEIGHT))
      // 监测 consul
      longPolling(serviceName)
    }
    //返回真实服务的地址（ip:port）可能是null
    //val sd = loadBalancerMap.get(serviceName).next("127.0.0.1") //加权后hash 请求ip
    val sd = loadBalancerMap.get(serviceName).next
    IsCondition.conditionException(sd.getPort < 0, "port can't less  0")
    logger.info("Real address is {}", sd)
    sd
  }

  private[this] def longPolling(serviceName: String) {
    new Thread(() => {
      var consulIndex: Long = -1
      do {
        val param = QueryParams.Builder.builder().setIndex(consulIndex).build()
        val healthyServices: Response[JList[HealthService]] = consulClient.getHealthServices(serviceName, true, param)
        consulIndex = healthyServices.getConsulIndex
        logger.debug("Consul index for {} is {}", serviceName, consulIndex)
        val healthServices = healthyServices.getValue
        logger.debug("Service addresses of {} is {}", serviceName, healthServices)
        loadBalancerMap.put(serviceName, buildLoadBalancer[WeightLoadBalancer[ServiceAddress]](healthServices, BalancerType.WEIGHT))
      } while (true)
    }).start()
  }


  /**
   *
   * @param healthServices 可用服务列表
   * @param balancerType   启用的负载均衡类型
   * @tparam L 预期类型
   * @return 实际类型
   */
  private[this] def buildLoadBalancer[L <: Loadbalancer[_]](healthServices: JList[HealthService],
                                                            balancerType: BalancerType): L = {
    val address = new JArrayList[ServiceAddress]()

    balancerType match {
      case BalancerType.RANDOM => {
        for (service <- healthServices.iterator()) {
          address.add(NormalServiceAddress(service.getService.getAddress, service.getService.getPort))
        }
        Try(new RandomLoadBalancer(address).asInstanceOf[L]).get
      }
      case BalancerType.WEIGHT => {
        for (service <- healthServices.iterator()) {
          address.add(new WeightServiceAddress(service.getService.getAddress, service.getService.getPort))
        }
        Try(new WeightLoadBalancer(address).asInstanceOf[L]).get
      }
    }
  }
}
