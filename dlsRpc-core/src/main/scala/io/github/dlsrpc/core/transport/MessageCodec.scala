package io.github.dlsrpc.core.transport

import java.util.{List => JList}

import io.github.dlsrpc.common.config.Configuration._
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec

/**
 * 解决tcp粘包的问题
 * 用来处理byte-to-message和message-to-byte。
 * 解码字节消息成POJO或编码POJO消息成字节，ByteToMessageCodec是一种组合，
 * 其等同于ByteToMessageDecoder和MessageToByteEncoder的组合。
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
class MessageCodec extends ByteToMessageCodec[Array[Byte]] {

  @throws[Exception]
  override protected def encode(channelHandlerContext: ChannelHandlerContext, msg: Array[Byte], byteBuf: ByteBuf): Unit = {
    val dataLength = msg.length
    byteBuf.writeInt(dataLength)
    byteBuf.writeBytes(msg)
  }

  @throws[Exception]
  override protected def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: JList[AnyRef]): Unit = {
    if (in.readableBytes < MESSAGE_LENGTH) return
    in.markReaderIndex
    val messageLength = in.readInt
    if (messageLength < 0) ctx.close
    if (in.readableBytes < messageLength) in.resetReaderIndex
    else {
      val messageBody = new Array[Byte](messageLength)
      in.readBytes(messageBody)
      out.add(messageBody)
    }
  }
}
