package io.growing.dls.centrel.discovery

import java.util.{ArrayList => JArrayList, List => JList}

import com.ecwid.consul.v1.health.model.HealthService
import com.ecwid.consul.v1.{QueryParams, Response}
import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.centrel.ConsulBuilder
import io.growing.dls.centrel.discovery.loadbalancer.RandomLoadBalancer
import io.growing.dls.meta.ServiceAddress
import io.growing.dls.utils.IsCondition

/**
 * 使用consul的服务发现
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
class ConsulServiceDiscovery(consulAddress: ServiceAddress) extends ServiceDiscovery with LazyLogging {

  final lazy val (consulClient, loadBalancerMap) = ConsulBuilder.checkAndBuild(consulAddress)

  //传进来的是service的类名
  override def discover(serviceName: String): ServiceAddress = {
    IsCondition.conditionException(serviceName == null, "service name can't be null")
    //    val realName = serviceName + "-" + Constants.CONSUL_ADDRESS
    if (!loadBalancerMap.containsKey(serviceName)) {
      //名字是serviceName-ip:port，    //TODO 优化过期接口
      val healthServices: JList[HealthService] = consulClient.getHealthServices(serviceName,
        true, QueryParams.DEFAULT).getValue
      loadBalancerMap.put(serviceName, buildLoadBalancer(healthServices))
      // 监测 consul
      longPolling(serviceName)
    }
    //返回真实服务的地址（ip:port）可能是null
    val sd = loadBalancerMap.get(serviceName).next
    IsCondition.conditionException(sd.port < 0, "port can't less  0")
    logger.info("real address is {}", sd)
    sd
  }

  private[this] def longPolling(serviceName: String) {
    new Thread(() => {
      var consulIndex: Long = -1
      do {
        val param = QueryParams.Builder.builder().setIndex(consulIndex).build()
        val healthyServices: Response[JList[HealthService]] = consulClient.getHealthServices(serviceName, true, param)
        consulIndex = healthyServices.getConsulIndex
        logger.debug("consul index for {} is {}", serviceName, consulIndex)
        val healthServices = healthyServices.getValue
        logger.debug("service addresses of {} is {}", serviceName, healthServices)
        loadBalancerMap.put(serviceName, buildLoadBalancer(healthServices))
      } while (true)
    }).start()
  }

  private[this] def buildLoadBalancer(healthServices: JList[HealthService]): loadbalancer.RandomLoadBalancer[ServiceAddress] = {
    import io.growing.dls.utils.ImplicitUtils.javaItToScalaIt // 隐式对象
    val address = new JArrayList[ServiceAddress]()
    for (service <- healthServices.iterator()) {
      address.add(ServiceAddress(service.getService.getAddress, service.getService.getPort))
    }
    new RandomLoadBalancer(address)
  }
}
