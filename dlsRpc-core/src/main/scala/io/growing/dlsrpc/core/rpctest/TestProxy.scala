package io.growing.dlsrpc.core.rpctest

import io.growing.dlsrpc.core.utils.DlsRpcInvoke

/**
 * 测试cglib
 * 需要测试jdk代理只需要增加Hello接口（特质），并使用HelloImpl继承它，并在ClientBuilder的build方法中调用super.getCglibProxy
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-11
 */
object TestProxy extends App {

  //jdk
  DlsRpcInvoke.publishService(8082, new HelloImpl())
}


object TestProxy2 extends App {

  //cglib
  DlsRpcInvoke.publishService(8083, new WorldImpl())
}

object TestJDKProxyClient extends App {

  //需要接口
  val hello: Hello = DlsRpcInvoke.obtainService("127.0.0.1", 8082, classOf[Hello])
  val ret: String = hello.sayHello("I am jdk proxy")
  println(ret)

}

object TestCglibProxyClient extends App {

  //不需要接口
  val hello: WorldImpl = DlsRpcInvoke.obtainService("127.0.0.1", 8083, classOf[WorldImpl])
  val ret: String = hello.sayHello("I am cglib proxy")
  println(ret)

}



