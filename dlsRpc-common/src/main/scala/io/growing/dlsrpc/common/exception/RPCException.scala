package io.growing.dlsrpc.common.exception

/**
 * 本项目的通用异常
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
case class RPCException(msg: String) extends RuntimeException(msg) {

  var throwable: Throwable = _

  def this(msg: String, throwable: Throwable) {
    this(msg)
    this.throwable = throwable
  }
}