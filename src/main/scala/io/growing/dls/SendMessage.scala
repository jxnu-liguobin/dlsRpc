package io.growing.dls

/**
 * 顶级发送消息接口
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
trait SendMessage {

  def sendMsg(msg: Array[Byte]): Unit
}
