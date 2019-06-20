package io.growing.dlsrpc.consul.loadbalancer

import java.util
import java.util.concurrent.ThreadLocalRandom
import java.util.{ArrayList => JArrayList, List => JList, Map => JMap}

import com.google.common.collect.Maps
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.config.DlsRpcConfiguration._
import io.growing.dlsrpc.common.metadata.WeightServiceAddress
import io.growing.dlsrpc.common.utils.ImplicitUtils._
import io.growing.dlsrpc.common.utils.IsCondition

/**
 * 加权随机法+加权Hash
 *
 * 没有完全支持泛型，只负责IP轮询，暂无容错
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-14
 * @param weightServiceAddresses 所有存活的服务
 */
class WeightLoadBalancer[T](val weightServiceAddresses: JList[T]) extends Loadbalancer[T]
  with LazyLogging {

  import WeightLoadBalancer._

  //临时服务地址，可能被修改
  @volatile
  protected[this] var serviceIps: JMap[WeightServiceAddress, Int] = _

  if (weightServiceAddresses.isEmpty || weightServiceAddresses.size() == 0) {
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
      for (_ <- 0 until weight) {
        serverList.add(server)
      }
    }

    val pos = ThreadLocalRandom.current.nextInt(serverList.size())
    serverList.get(pos).asInstanceOf[T]
  }

  override def next(remoteIp: String): T = {
    IsCondition.conditionException(SERVICE_IP_LIST.size() == 0, "can't use default ip because param error in dlsRpc.conf")
    val serverMap: JMap[WeightServiceAddress, Int] = serviceIps
    val it: util.Iterator[WeightServiceAddress] = serverMap.keySet().iterator()
    val serverList = new JArrayList[WeightServiceAddress]
    while (it.hasNext) {
      val server = it.next()
      val weight = serverMap.get(server)
      for (_ <- 0 until weight) {
        serverList.add(server)
      }
    }
    val hashCode = remoteIp.hashCode
    val serverListSize = serverList.size
    val pos = hashCode % serverListSize
    serverList.get(pos).asInstanceOf[T]
  }
}

//单独测试这个负载均衡时增加 extends App，到运行TestConsulService测试consul服务发现和注册时必须去掉，否则defaultWeightServiceAddress不会被初始化
//因为val 在main启动的时候初始化，而你又继承了App，却没有启动，所以代码不会被执行。
//我发现一个有趣的问题刚好和JVM类加载那章的变量初始化顺序相同。写个半生对象，object中初始化配置 final val s = lambda，测试时我把object extends App 了，
//这样测试这个object是OK的，结果我去测试其他object extends App 并调用这个object，初始化会被执行但是值是原始值，
//Int就是0这种，这符合深入java JVM中说的final先被初始化为类型初始值，这时final虽然被初始化但是值是无效。
//scala中原因是object的val只会在main方法运行时被初始化（叫赋值初始化），而你extends了App但是不用，
//那么其他代码调用这个object时该代码也仅仅被初始化并不会被赋值初始化，拿到值都是0 ，null 0,0这种，
//最后解决就是去掉被依赖对象的extends App 即时这个只是测试时用到的main方法但是最后跑整体代码时这个是必须要去掉，
object WeightLoadBalancer {

  //默认权值
  private val weight = 5

  //配置了默认服务ip优先级高一点
  private val high_weight = 10

  //默认WEB端口
  private val port = WEB_SERVER_PORT

  //默认的服务列表，从配置文件读取，表示启用的服务列表，隐式去重
  private final lazy val defaultWeightServiceAddress: JMap[WeightServiceAddress, Int] = Maps.newConcurrentMap()
  SERVICE_IP_LIST.forEach(x => defaultWeightServiceAddress.put(new WeightServiceAddress(x, port, high_weight), high_weight))
  SERVICE_IP_LIST.forEach(x => IsCondition.conditionException(!x.toString.matches(IP_PATTERN), "not an valid format like ip"))

  //Test data
  val s = new util.ArrayList[WeightServiceAddress]()
  s.add(new WeightServiceAddress("127.0.0.1", port, 2))
  s.add(new WeightServiceAddress("127.0.1.5", port, 1))
  s.add(new WeightServiceAddress("127.7.1.2", port, 1))
  s.add(new WeightServiceAddress("127.1.1.2", port, 1))
  s.add(new WeightServiceAddress("192.168.1.1", port, 1))
  println(new WeightLoadBalancer(s).next)
  println(new WeightLoadBalancer(s).next("127.0.5.1"))
}