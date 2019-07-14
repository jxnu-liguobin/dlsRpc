package io.growing.dlsrpc.consul.task

import java.util.{List => JList}

import com.ecwid.consul.v1.health.HealthServicesRequest
import com.ecwid.consul.v1.health.model.HealthService
import com.ecwid.consul.v1.{ConsulClient, QueryParams, Response}
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.enums.BalancerType
import io.growing.dlsrpc.common.metadata.NormalServiceAddress
import io.growing.dlsrpc.consul.commons.ConsulBuilder
import io.growing.dlsrpc.consul.loadbalancer.RandomLoadBalancer

/**
 * 初始化刷新，定时检查并更新服务
 *
 * TODO 轮询检查服务地址是否失效
 *
 * @author 梦境迷离
 * @version 1.1, 2019-07-13
 */
class AllScheduledRefresh(consulClient: ConsulClient) extends Runnable with ScheduledRefresh with LazyLogging {
  override def run(): Unit = {
    if (!ConsulBuilder.loadBalancerMapContext.isEmpty) {
      try {
        ConsulBuilder.loadBalancerMapContext.forEach { (serviceName, _) =>
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
          val size = serviceLoadBalancer match {
            case Some(sb) => sb.getServiceAddressMap.size()
            case None => 0
          }
          logger.info(s"Current service : {$serviceName} has {$size} instances")
          ConsulBuilder.loadBalancerMapContext.put(serviceName, serviceLoadBalancer)
        }
      }
      catch {
        case e: Exception => logger.error(s"Update LoadBalancerMap fail when serviceRefresh, cause by : {$e.getMessage}")
      }
      logger.info("LoadBalancerMap is refresh success")
    }
  }
}