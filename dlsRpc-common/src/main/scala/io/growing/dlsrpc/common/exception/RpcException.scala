package io.growing.dlsrpc.common.exception

/**
 * 本项目的通用异常
 *
 * 除了通过条件主动抛出的异常，其他异常的message第一个字母都是大写
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
case class RpcException(msg: String) extends RuntimeException(msg) {

  var throwable: Throwable = _

  def this(msg: String, throwable: Throwable) {
    this(msg)
    this.throwable = throwable
  }
}