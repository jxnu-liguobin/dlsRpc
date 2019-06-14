package io.growing.dlsrpc.core.client

import io.growing.dlsrpc.common.exception.{ProxyException, RPCException}
import io.growing.dlsrpc.common.utils.SuperClassUtils

/**
 * 客户端建造器
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
class ClientBuilder[T] private(clientClass: Class[T]) extends Client[ClientBuilder[T], T] {

  /**
   * 打开通道并创建代理对象
   *
   * @return 代理对象
   */
  def build: T = {
    try {
      super.setClientClass(clientClass)
      super.start()
      SuperClassUtils.matchProxy(clientClass) match {
        case "CGLIB" => super.cglibProxy
        case "JDK" => super.proxy
      }
    }
    catch {
      case e: ClassCastException => {
        throw new ProxyException("class cast fail", e)
      }
      case ex: Exception => {
        throw new RPCException("Other error", ex)
      }
    }
  }
}

object ClientBuilder {

  /**
   * 静态方法，获取构造器
   *
   * @param interfaceClass 需要代理对象的接口的class
   * @tparam T 代理对象类型
   * @return
   */
  def builderWithClass[T](interfaceClass: Class[T]) = new ClientBuilder[T](interfaceClass)
}
