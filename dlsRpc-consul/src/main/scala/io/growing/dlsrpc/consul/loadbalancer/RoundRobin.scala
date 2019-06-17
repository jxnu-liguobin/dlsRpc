package io.growing.dlsrpc.consul.loadbalancer

import java.util
import java.util.concurrent.ThreadLocalRandom
import java.util.{ArrayList => JArrayList, List => JList, Map => JMap}

import com.google.common.collect.Maps
import com.typesafe.scalalogging.LazyLogging
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
 * @param weightServiceAddresses 所有存活的服务
 */
class RoundRobin[T] private(val weightServiceAddresses: JList[T]) extends Loadbalancer[T]
  with LazyLogging {

  import RoundRobin._

  //临时服务地址，可能被修改
  @volatile
  protected[this] var serviceIps: JMap[WeightServiceAddress, Int] = _

  if (weightServiceAddresses.isEmpty) {
    serviceIps = defaultWeightServiceAddress
    logger.info("Number of incoming empty, using the default configuration list {}", serviceIps.size())
  } else {
    logger.info("Number of incoming is {}", weightServiceAddresses.size())
    serviceIps = convertToMap(weightServiceAddresses)
    logger.info("Number of merged is {}", serviceIps.size())
  }

  /**
   * 如果有传入，则合并默认。
   * 传入的拥有默认优先级但是在conf中配置了ip的服务将得到更高优先级
   *
   * @param weightServiceAddresses
   * @return
   */
  private[this] def convertToMap(weightServiceAddresses: JList[T]): JMap[WeightServiceAddress, Int] = {
    //对传进来的进行放入map中，并对默认配置进行合并
    val ms: JMap[WeightServiceAddress, Int] = Maps.newConcurrentMap()
    for (service <- weightServiceAddresses.iterator()) {
      val s: WeightServiceAddress = service.asInstanceOf[WeightServiceAddress]
      ms.put(s, s.getWeight)
    }
    //以传入的存活的服务为准，若配置文件中也配置了，则会增加该服务的权值。注意 Key 需要实现 hashcode equals
    //服务存活但是没有配置，会有低优先级的默认值
    //存活并且配置了，权值会增加，服务端任务该服务地址是用户更想去取得的地址
    val mergeMap: JMap[WeightServiceAddress, Int] = defaultWeightServiceAddress ++ ms.map(m =>
      m._1 -> (m._2 + defaultWeightServiceAddress.getOrElse(m._1, weight)))
    mergeMap
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

object RoundRobin extends App {

  //默认权值
  private val weight = 2

  //配置了默认服务ip优先级高一点
  private val high_weight = 10

  //默认WEB端口
  private val port = 9000

  //默认的服务列表，从配置文件读取，表示启用的服务列表，隐式去重
  private final lazy val defaultWeightServiceAddress: JMap[WeightServiceAddress, Int] = Maps.newConcurrentMap()
  SERVICE_IP_LIST.foreach(x => defaultWeightServiceAddress.put(new WeightServiceAddress(x, port, high_weight), high_weight))
  SERVICE_IP_LIST.foreach(x => IsCondition.conditionException(!x.toString.matches(IP_PATTERN), "not an valid format like ip"))

  //Test data
  val s = new util.ArrayList[WeightServiceAddress]()
  s.add(new WeightServiceAddress("127.0.0.1", port, 0))
  s.add(new WeightServiceAddress("127.0.1.5", port, 0))
  s.add(new WeightServiceAddress("127.7.1.2", port, 0))
  s.add(new WeightServiceAddress("127.1.1.2", port, 0))
  println(new RoundRobin(s).next)
}