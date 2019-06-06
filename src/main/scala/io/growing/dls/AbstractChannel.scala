package io.growing.dls

/**
 * 顶级通道（Socket）接口（特质）
 *
 * client和server都需要关闭方法
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
trait AbstractChannel {

  def shutdown(): Unit
}
