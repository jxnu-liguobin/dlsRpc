package io.growing.dlsrpc.consul.loadbalancer

import java.util
import java.util.concurrent.{ConcurrentHashMap, ThreadLocalRandom}
import java.util.{ArrayList => JArrayList, List => JList, Map => JMap}

import io.growing.dlsrpc.common.config.DlsRpcConfiguration._
import io.growing.dlsrpc.common.utils.ImplicitUtils._
import io.growing.dlsrpc.common.utils.IsCondition

/**
 * 加权轮询法
 *
 * 没有完全支持泛型，只负责IP轮询，暂无容错
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-14
 */
class RoundRobin[T](weightServiceAddresses: JList[T]) extends Loadbalancer[T] {


  import RoundRobin._

  //临时服务地址，可能被修改
  @volatile
  protected[this] var serviceIps: JMap[WeightServiceAddress, Int] = _

  if (weightServiceAddresses.isEmpty) {
    serviceIps = defaultWeightServiceAddress
  } else {
    serviceIps = convertToMap(weightServiceAddresses)
  }

  /**
   * 如果有传入，则合并默认
   *
   * @param weightServiceAddresses
   * @return
   */
  private[this] def convertToMap(weightServiceAddresses: JList[T]): JMap[WeightServiceAddress, Int] = {
    val ms: JMap[WeightServiceAddress, Int] = new ConcurrentHashMap[WeightServiceAddress, Int]
    for (service <- weightServiceAddresses.iterator()) {
      val s: WeightServiceAddress = service.asInstanceOf[WeightServiceAddress]
      ms.put(new WeightServiceAddress(s.getIp, s.getPort, s.getWeight + weight), weight)
    }
    ms.putAll(defaultWeightServiceAddress)
    ms
  }

  override def next: T = {
    IsCondition.conditionException(SERVICE_IP_LIST.size() == 0, "can't use default ip and set error in dlsRpc.conf")
    //给它设置相等的权值
    val serverMap: JMap[WeightServiceAddress, Int] = serviceIps
    //獲取ip列表list
    val it: util.Iterator[WeightServiceAddress] = serverMap.keySet().iterator()
    val serverList = new JArrayList[WeightServiceAddress]
    while (it.hasNext) {
      val server = it.next()
      val weight = serverMap.get(server)
      for (i <- 0 until weight) {
        serverList.add(server)
      }
    }
    serverList.get(ThreadLocalRandom.current.nextInt(serviceIps.size())).asInstanceOf[T]
  }

}

object RoundRobin {

  //默认权值
  private val weight = 2

  //默认WEB端口
  private val port = 9000

  //默认的服务列表，从配置文件读取
  private final lazy val defaultWeightServiceAddress: JMap[WeightServiceAddress, Int] = new ConcurrentHashMap[WeightServiceAddress, Int]
  SERVICE_IP_LIST.forEach(x => defaultWeightServiceAddress.put(new WeightServiceAddress(x, port, weight), weight))
  SERVICE_IP_LIST.forEach(x => {
    IsCondition.conditionException(!x.toString.matches(IP_PATTERN), "not an valid format like ip")
  })
}