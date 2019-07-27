package io.github.dlsrpc.common.exception

/**
 * cglib jdk proxy 异常
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-12
 */
case class ProxyException(message: String) extends RuntimeException(message) {

  var throwable: Throwable = _

  def this(msg: String, throwable: Throwable) {
    this(msg)
    this.throwable = throwable
  }
}