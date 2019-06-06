package io.growing.dls.server

import java.io.IOException
import java.util.concurrent.Executor

import io.growing.dls.{AbstractChannel, Protocol}

/**
 * 服务端通道接口
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
trait ServerChannel extends AbstractChannel {

  /**
   * 开启服务端通道，绑定监听端口
   *
   * @param port           端口
   * @param executor       执行器
   * @param protocol       传输协议
   * @param messageHandler 消息处理器
   * @throws IOException IO异常
   */
  @throws[IOException]
  def start(port: Int, executor: Executor, protocol: Protocol, messageHandler: ServerMessageHandler): Unit

}
