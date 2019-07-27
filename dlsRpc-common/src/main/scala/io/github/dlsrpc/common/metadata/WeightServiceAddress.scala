package io.github.dlsrpc.common.metadata

import java.util.Objects

/**
 * 附有加权所需要的权值的服务器地址
 *
 * 现在已经成为默认属性
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-14
 */
class WeightServiceAddress(val ip: String, val port: Int, weight: Int = 5)
  extends ServiceAddress(ip, port) {

  require(weight > 0) //权值必须大于0

  def getWeight = this.weight

  override def getIp = this.ip

  override def getPort = this.port

  override def toString: String = super.toString

  //忽略权值
  override def hashCode(): Int = Objects.hash(ip.asInstanceOf[Object], port.asInstanceOf[Object])

  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case w: WeightServiceAddress => {
        this.ip.equals(w.getIp) && this.port.equals(w.getPort)
      }
      case _ => false
    }
  }
}