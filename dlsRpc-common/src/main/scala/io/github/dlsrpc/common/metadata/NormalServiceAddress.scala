package io.github.dlsrpc.common.metadata

/**
 * 服务注册使用
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-08
 */
case class NormalServiceAddress(ip: String, port: Int) extends ServiceAddress(ip, port) {

  override def getIp: String = ip

  override def getPort: Int = port

  override def toString: String = s"$ip:$port"

  /**
   * 直接将tcp参数映射为可用的服务端调用地址
   *
   * @param tcp localhost:8080
   */
  def this(tcp: String) = {
    this(tcp.split(":")(0), tcp.split(":")(1).toInt)
  }
}