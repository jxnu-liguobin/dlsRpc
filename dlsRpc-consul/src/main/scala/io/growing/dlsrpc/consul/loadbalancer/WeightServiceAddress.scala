package io.growing.dlsrpc.consul.loadbalancer

import java.util.Objects

import io.growing.dlsrpc.common.metadata.ServiceAddress

/**
 * 附有加权值所需要的权值的服务器地址
 *
 * 一般就是服务端用，所以写在这个模块
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-14
 */
class WeightServiceAddress(override val ip: String, override val port: Int, weight: Int)
  extends ServiceAddress(ip, port) {

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