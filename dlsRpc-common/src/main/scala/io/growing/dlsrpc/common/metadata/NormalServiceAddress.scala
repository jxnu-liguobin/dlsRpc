package io.growing.dlsrpc.common.metadata

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
}