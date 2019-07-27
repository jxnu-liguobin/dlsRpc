package io.github.dlsrpc.consul.task

import java.util.{List => JList}

import com.ecwid.consul.v1.health.HealthServicesRequest
import com.ecwid.consul.v1.health.model.HealthService
import com.ecwid.consul.v1.{ConsulClient, QueryParams, Response}
import com.typesafe.scalalogging.LazyLogging
import io.github.dlsrpc.common.enums.BalancerType
import io.github.dlsrpc.common.metadata.NormalServiceAddress
import io.github.dlsrpc.consul.commons.ConsulBuilder
import io.github.dlsrpc.consul.loadbalancer.RandomLoadBalancer

/**
 * 刷新单个
 *
 * @author 梦境迷离
 * @version 1.0, 2019-07-13
 */
class SingleScheduledRefresh(serviceName: String, consulClient: ConsulClient)
  extends Runnable with ScheduledRefresh with LazyLogging {

  override def run(): Unit = {
    if (!ConsulBuilder.loadBalancerMapContext.isEmpty) {
      try {
        var consulIndex: Long = -1
        val param: QueryParams = new QueryParams(wait_time, consulIndex)
        val request: HealthServicesRequest = HealthServicesRequest.
          newBuilder().setPassing(true).setQueryParams(param).build
        val healthyServices: Response[JList[HealthService]] = consulClient.getHealthServices(serviceName, request)
        consulIndex = healthyServices.getConsulIndex
        logger.info(s"Consul index for {$serviceName} is {$consulIndex}")
        val healthServices = healthyServices.getValue
        logger.debug(s"Service addresses of {$serviceName} is {$healthServices}")
        //把查询到的服务构建成LoadBalancer对象
        val serviceLoadBalancer = ConsulBuilder.buildLoadBalancer[RandomLoadBalancer[NormalServiceAddress]](healthServices, BalancerType.WEIGHT)
        ConsulBuilder.loadBalancerMapContext.put(serviceName, serviceLoadBalancer)
      } catch {
        case e: Exception => logger.error(s"Update LoadBalancerMap failed when serviceRefresh, cause by : {$e.getMessage}")
      }
      logger.info("LoadBalancerMap is refresh success")
    } else {
      logger.warn("LoadBalancerMap is null, may no any services")
    }
  }
}
