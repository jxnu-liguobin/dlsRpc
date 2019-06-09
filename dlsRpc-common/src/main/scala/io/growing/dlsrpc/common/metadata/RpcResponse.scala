package io.growing.dlsrpc.common.metadata

import scala.beans.BeanProperty

/**
 * 返回
 *
 * 兼容Java
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
sealed case class RpcResponse() {

  //请求id
  @BeanProperty
  var requestId: Long = _
  //错误
  @BeanProperty
  var error: Throwable = _
  //返回值
  @BeanProperty
  var result: AnyRef = _

  def this(requestId: Long, error: Throwable, result: AnyRef) {
    this()
    this.requestId = requestId
    this.result = result
    this.error = error
  }
}
