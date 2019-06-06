package io.growing.dls.protocol

import io.growing.dls.Protocol
import io.netty.handler.codec.http2.Http2FrameCodec

/**
 * http2 转化为帧，目前没用
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
final class Http2Protocol(server: Boolean) extends Http2FrameCodec(server) with Protocol {
  //给guice用
  def this() {
    this(true)
  }
}
