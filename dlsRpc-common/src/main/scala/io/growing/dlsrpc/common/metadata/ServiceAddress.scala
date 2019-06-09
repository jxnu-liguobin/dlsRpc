package io.growing.dlsrpc.common.metadata

/**
 * 服务注册使用
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
case class ServiceAddress(ip: String, port: Int) {

  /**
   * 暴露ip
   *
   * @return
   */
  def getIp: String = ip

  /**
   * 暴露port
   *
   * @return
   */
  def getPort: Int = port

  /**
   * 特殊的toString，可以直接使用作为tcp的参数
   *
   * @return
   */
  override def toString: String = s"$ip:$port"
}