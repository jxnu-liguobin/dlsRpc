package io.github.dlsrpc.core.client

import io.github.dlsrpc.common.enums.ProxyType
import io.github.dlsrpc.common.exception.{ProxyException, RpcException}
import io.github.dlsrpc.common.utils.SuperClassUtils

/**
 * 客户端建造器
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-05
 */
class ClientBuilder[T] private(clientClass: Class[T]) extends Client[ClientBuilder[T], T](clientClass) {

  /**
   * 打开通道并创建代理对象
   *
   * @return 代理对象
   */
  def build: T = {
    try {
      super.start()
      SuperClassUtils.matchProxy(clientClass) match {
        case ProxyType.CGLIB => super.cglibProxy
        case ProxyType.JDK => super.proxy
      }
    }
    catch {
      case e: ClassCastException => {
        throw new ProxyException("class cast fail", e)
      }
      case ex: Exception => {
        throw new RpcException("Other error", ex)
      }
    }
  }

  def stopClient: Unit = super.shutdown()
}

object ClientBuilder {

  /**
   * 静态方法，获取构造器
   *
   * @param className 需要代理对象的接口的class或者类
   * @tparam T 代理对象类型
   * @return
   */
  def builderWithClass[T](className: Class[T]) = new ClientBuilder[T](className)
}
