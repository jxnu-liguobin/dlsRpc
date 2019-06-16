package io.growing.dlsrpc.core.api

/**
 * 顶级通道（Socket）接口（特质）
 *
 * client和server都需要关闭方法
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
trait AbstractChannel {


  /**
   * 关闭，默认回调channel.close
   */
  def shutdown(): Unit
}
