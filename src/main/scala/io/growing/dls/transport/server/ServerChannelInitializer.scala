package io.growing.dls.transport.server

import java.util.concurrent.Executor

import com.typesafe.scalalogging.LazyLogging
import io.growing.dls.server.ServerMessageHandler
import io.growing.dls.transport.MessageCodec
import io.netty.channel.socket.SocketChannel
import io.netty.channel.{ChannelInitializer, ChannelPipeline}
import io.netty.handler.logging.{LogLevel, LoggingHandler}

/**
 * 服务端通道的初始化
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
class ServerChannelInitializer(executor: Executor, messageHandler: ServerMessageHandler)
  extends ChannelInitializer[SocketChannel] with LazyLogging {

  override def initChannel(c: SocketChannel): Unit = {
    val pipeline: ChannelPipeline = c.pipeline
    pipeline.addLast("log", new LoggingHandler(LogLevel.INFO))
    pipeline.addLast("messageCodec", new MessageCodec)
    pipeline.addLast("server-message-handler", new NettyServerMessageHandler(executor, messageHandler))
    logger.info("ServerChannelInitializer  initChannel ... ")
  }
}
