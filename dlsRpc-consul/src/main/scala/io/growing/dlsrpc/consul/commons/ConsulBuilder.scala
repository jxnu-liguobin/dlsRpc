package io.growing.dlsrpc.consul.commons

import java.util.concurrent.{ConcurrentMap, Executors, ScheduledExecutorService, TimeUnit}
import java.util.{ArrayList => JArrayList, List => JList}

import com.ecwid.consul.v1.health.model.HealthService
import com.ecwid.consul.v1.{ConsulClient, ConsulRawClient}
import com.google.common.collect.Maps
import io.growing.dlsrpc.common.config.Configuration
import io.growing.dlsrpc.common.config.Configuration._
import io.growing.dlsrpc.common.enums.BalancerType
import io.growing.dlsrpc.common.enums.BalancerType.BalancerType
import io.growing.dlsrpc.common.exception.RpcException
import io.growing.dlsrpc.common.metadata.{NormalServiceAddress, ServiceAddress, WeightServiceAddress}
import io.growing.dlsrpc.common.utils.CheckCondition
import io.growing.dlsrpc.common.utils.ImplicitUtils._
import io.growing.dlsrpc.consul.loadbalancer.{LoadBalancer, RandomLoadBalancer, WeightLoadBalancer}
import io.growing.dlsrpc.consul.task.{AllScheduledRefresh, SingleScheduledRefresh}

/**
 * 封装构造逻辑
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-09
 */
object ConsulBuilder {

  //保留线程池大小
  private final val corePoolSize = 20

  //启动多久后启动定时任务
  private final val initialDelay = 10

  //每个多久运行一次定时任务
  private final val delay = 15

  /**
   * 根据consul地址构造发现客户端
   *
   * @param consulAddress ip port
   * @return
   */
  @deprecated
  def buildDiscover(consulAddress: ServiceAddress): (ConsulClient, ConcurrentMap[String, RandomLoadBalancer[ServiceAddress]]) = {
    CheckCondition.conditionException(!consulAddress.toString.matches(PATTERN), "not an valid format like ip:port")
    CheckCondition.conditionException(consulAddress.getPort < 0, "port can't less  0")
    val rawClient = new ConsulRawClient(consulAddress.getIp, consulAddress.getPort)
    val consulClient = new ConsulClient(rawClient)
    val loadBalancerMap = Maps.newConcurrentMap[String, RandomLoadBalancer[ServiceAddress]]
    (consulClient, loadBalancerMap)
  }

  /**
   * 根据consul地址构造注册客户端
   *
   * @param consulAddress
   * @return
   */
  @deprecated
  def buildRegistry(consulAddress: ServiceAddress): ConsulClient = {
    CheckCondition.conditionException(!consulAddress.toString.matches(PATTERN), "not an valid format like ip:port")
    CheckCondition.conditionException(consulAddress.getPort < 0, "port can't less  0")
    val rawClient = new ConsulRawClient(consulAddress.getIp, consulAddress.getPort)
    val consulClient = new ConsulClient(rawClient)
    consulClient
  }

  /**
   * 统一封装并返回
   *
   * @return
   */
  def checkAndBuild: ConsulClient = {
    //只支持权值+Hash Ip
    val consulAddress = NormalServiceAddress(Configuration.CONSUL_ADDRESS_IP, Configuration.CONSUL_ADDRESS_PORT)
    consulAddress match {
      case s: NormalServiceAddress => {
        CheckCondition.conditionException(!s.toString.matches(PATTERN), "not an valid format like ip:port")
        CheckCondition.conditionException(s.getPort < 0, "port can't less  0")
        lazy val rawClient = new ConsulRawClient(s.getIp, s.getPort)
        new ConsulClient(rawClient)
      }
      case _ => {
        //TODO
        throw RpcException("no matching type")
      }
    }
  }

  /**
   * 构造包含负载均衡器的服务所有的可用列表
   *
   * @param healthServices 可用服务列表
   * @param balancerType   启用的负载均衡类型
   * @tparam L 预期类型 协变
   * @return 实际类型 类型提升
   */
  def buildLoadBalancer[L <: LoadBalancer[_]](healthServices: JList[HealthService],
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

  //保持的20个
  private final lazy val executorService: ScheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize)

  /**
   * 刷新指定的服务
   *
   * @param serviceName  serviceName
   * @param consulClient consulClient
   */
  def execSchedulesTask(serviceName: String, consulClient: ConsulClient) {
    lazy val scheduleTask = new SingleScheduledRefresh(serviceName, consulClient)
    ConsulBuilder.executorService.scheduleWithFixedDelay(scheduleTask,
      initialDelay, delay, TimeUnit.SECONDS)
  }

  /**
   * 刷新所有
   *
   * @param consulClient
   */
  def execSchedulesTask(consulClient: ConsulClient) {
    lazy val scheduleTask = new AllScheduledRefresh(consulClient)
    ConsulBuilder.executorService.scheduleWithFixedDelay(scheduleTask,
      initialDelay, delay, TimeUnit.SECONDS)
  }

  private final lazy val loadBalancerMap = Maps.newConcurrentMap[String, Option[LoadBalancer[ServiceAddress]]]()

  val loadBalancerMapContext: ConcurrentMap[String, Option[LoadBalancer[ServiceAddress]]] = loadBalancerMap

}
