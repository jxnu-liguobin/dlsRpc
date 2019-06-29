package io.growing.dlsrpc.core.rpctest

import io.growing.dlsrpc.common.config.Configuration
import io.growing.dlsrpc.core.DlsRpc
import io.growing.dlsrpc.core.server.ServerBuilder

/**
 * 测试cglib、netty
 * 需要测试jdk代理只需要增加Hello接口（特质），并使用HelloImpl继承它
 * 在ClientBuilder的build方法中会自动根据是否有接口调用super.cglibProxy、super.proxy
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-11
 */
object TestProxyNetty extends App {

  //jdk
  DlsRpc.publishService(8082, new HelloImpl())
}


object TestProxy2 extends App {

  //cglib
  DlsRpc.publishService(8083, new WorldImpl())
}


object TestJDKProxyClient extends App {

  //需要接口
  val hello: Hello = DlsRpc.obtainService("127.0.0.1", 8082, classOf[Hello])
  val ret: String = hello.sayHello("I am jdk proxy")
  println(ret)
}


object TestCglibProxyClient extends App {

  //不需要接口
  val hello: WorldImpl = DlsRpc.obtainService("127.0.0.1", 8083, classOf[WorldImpl])
  val ret: String = hello.sayHello("I am cglib proxy")
  println(ret)

}

/**
 * 测试netty
 */

object NettTest extends App {

  var server: ServerBuilder = _

  def TestNettyServer {
    server = DlsRpc.getServerBuilder(8081, new HelloImpl())
    server.build.start()
  }


  def TestNettyClient {
    val client = DlsRpc.getClientBuilder("127.0.0.1", 8081, classOf[Hello])
    val hello = client.build
    val ret = hello.sayHello("I am netty")
    println(ret)
    Thread.sleep(3000)
    client.stopClient
    Thread.sleep(3000)
    server.stopServer
  }

  new Thread(() => TestNettyServer).start()
  new Thread(() => TestNettyClient).start()

}

/**
 * 集成测试服务发布、注册、RPC
 */
object TestRpcServer extends App {
  //默认发布到本地
  //  DlsRpcInvoke.getServerBuilder(DlsRpcConfiguration.WEB_SERVER_PORT, Seq(new HelloImpl())).build.start()
  DlsRpc.getServerBuilder(Configuration.WEB_SERVER_PORT, Seq(new WorldImpl())).build.start()
}

object TestRpcClient extends App {
  val hello: WorldImpl = DlsRpc.getClientBuilder(classOf[WorldImpl]).build
  println(hello.sayHello("I am a dog"))
}



