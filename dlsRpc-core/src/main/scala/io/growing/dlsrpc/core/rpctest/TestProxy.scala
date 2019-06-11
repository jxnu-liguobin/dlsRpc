package io.growing.dlsrpc.core.rpctest

import java.util

import io.growing.dlsrpc.core.utils.DlsRpcInvoke

/**
 * 测试cglib
 * 需要测试jdk代理只需要增加Hello接口（特质），并使用HelloImpl继承它，并在ClientBuilder的build方法中调用super.getCglibProxy
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-11
 */
object TestProxy extends App {

  DlsRpcInvoke.publishService(8082, new HelloImpl())

}

//默认是cglib
object TestProxyClient extends App {

  //不需要接口
  val hello: HelloImpl = DlsRpcInvoke.obtainService("127.0.0.1", 8082, classOf[HelloImpl])
  for (i <- 1 to 3) {
    val ret: String = hello.sayHello("I am cglib proxy")
    println(ret)
  }

}

object TestJDKProxyClient extends App {

  //  //需要接口
  //  val hello: Hello = DlsRpcInvoke.obtainService("127.0.0.1", 8081, classOf[Hello])
  //  for (i <- 1 to 3) {
  //    val ret: String = hello.sayHello("I am jdk proxy")
  //    println(ret)
  //  }

}

//获取类实现的接口
object Test extends App {

  getSuperInterfaces(new HelloImpl)

  def getSuperInterfaces(obj: AnyRef): Unit = {
    val ret = new util.ArrayList[String]()
    val seq = obj.getClass.getInterfaces
    for (i <- seq) {
      println(i.getSimpleName)
      ret.add(i.getSimpleName)
    }
  }
}



