package io.growing.dlsrpc.common.config

import java.util
import java.util.{List => JList}

import com.typesafe.config.{Config, ConfigFactory}
import io.growing.dlsrpc.common.exception.RpcException

import scala.util.Try

/**
 * 加载配置
 *
 * TODO 后续应当检测，若有配置文件则应该覆盖dlsRpc.conf，Config组件默认支持
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-13
 */
object Configuration {
  //去掉 extends App 不然拿不到值

  private final var config: Config = _

  /**
   * 若调用过该方法，则使用此配置
   *
   * @param userConfigName 配置必须在config目录下，若有子目录需要加/folder/userConfigName.conf
   */
  def config(userConfigName: String) {
    if (userConfigName == null || "".equals(userConfigName)) {
      throw RpcException("Config name can't be null")
    }
    config = ConfigFactory.load(userConfigName)
  }

  if (config == null) {
    config = ConfigFactory.load("dlsRpc.conf")
  }
  //TODO 路径处理

  //读取配置文件
  final val CONSUL_ADDRESS_IP: String = Try(config.getString("dlsrpc.consul.host")).getOrElse(DefaultConstants.CONSUL_ADDRESS_IP)
  final val CONSUL_ADDRESS_PORT: Int = Try(config.getInt("dlsrpc.consul.port")).getOrElse(DefaultConstants.CONSUL_ADDRESS_PORT)
  final val CONSUL_ENABLE: Boolean = Try(config.getBoolean("dlsrpc.consul.enable")).getOrElse(DefaultConstants.CONSUL_ENABLE)
  final val TIME_WAIT: Int = Try(config.getInt("dlsrpc.client.timeout")).getOrElse(DefaultConstants.TIME_WAIT)
  final val REQUEST_START_VALUE: Int = Try(config.getInt("dlsrpc.client.request-start-value")).getOrElse(DefaultConstants.REQUEST_START_VALUE)
  final val MESSAGE_LENGTH: Int = Try(config.getInt(" dlsrpc.http.message-length")).getOrElse(DefaultConstants.MESSAGE_LENGTH)
  final val PATTERN: String = Try(config.getString("dlsrpc.consul.adress-pattern")).getOrElse(DefaultConstants.PATTERN)
  final val IP_PATTERN: String = Try(config.getString("dlsrpc.consul.ip-pattern")).getOrElse(DefaultConstants.IP_PATTERN)
  final val PACKAGE_SERVICE: String = Try(config.getString("dlsrpc.consul.registry.package-service")).getOrElse(DefaultConstants.PACKAGE_SERVICE)
  final val DEFAULT_DISCOVER_ADDRESS: String = Try(config.getString("dlsrpc.server.address.default")).getOrElse(DefaultConstants.DEFAULT_DISCOVER_ADDRESS)
  final val CGLIB_PROXY: Boolean = Try(config.getBoolean("dlsrpc.proxy.mode.cglib-proxy")).getOrElse(DefaultConstants.CGLIB_PROXY)
  final val FORCE_CGLIB_PROXY: Boolean = Try(config.getBoolean("dlsrpc.proxy.mode.force-cglib-proxy")).getOrElse(DefaultConstants.FORCE_CGLIB_PROXY)
  final val WEB_SERVER_PORT: Int = Try(config.getInt("dlsrpc.server.port")).getOrElse(DefaultConstants.WEB_SERVER_PORT)
  final val WEB_SERVER_IP: String = Try(config.getString("dlsrpc.server.ip")).getOrElse(DefaultConstants.WEB_SERVER_IP)
  final val DEFAULT_WEIGHT: Int = Try(config.getInt("dlsrpc.server.balancer-weight")).getOrElse(DefaultConstants.DEFAULT_WEIGHT)
  final val CONSUL_INTERVAL: String = Try(config.getString("dlsrpc.consul.interval")).getOrElse(DefaultConstants.CONSUL_INTERVAL)

  private final lazy val default: JList[String] = new util.ArrayList[String]()
  default.add(DEFAULT_DISCOVER_ADDRESS)

  final val SERVICE_IP_LIST: JList[String] = Try(config.getStringList("dlsrpc.consul.server.address")).getOrElse(default)

  //默认配置，外界不可访问
  private object DefaultConstants {

    final val CONSUL_INTERVAL = "1s"

    final val DEFAULT_WEIGHT = 5

    //客户端超时时间
    final val TIME_WAIT = 30 * 1000

    //请求id，一般从 0 开始自增
    final val REQUEST_START_VALUE = 0

    final val MESSAGE_LENGTH = 4

    final val PATTERN = "(?:(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5]):\\d{0,5}"

    final val IP_PATTERN = "(?:(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5])"

    //默认注册的包下的service
    final val PACKAGE_SERVICE = "io.growing.dlsrpc"

    //服务发现与注册中心，默认ip是本地consul
    final val CONSUL_ADDRESS_IP = "127.0.0.1"

    //consul默认端口
    final val CONSUL_ADDRESS_PORT = 8500

    //是否开启服务注册
    final val CONSUL_ENABLE = true

    //默认是服务调用地址就是本地，当不开启consul时，使用此地址作为服务提供者的暴露地址
    final val DEFAULT_DISCOVER_ADDRESS = "localhost:8080"

    //服务提供者暴露的端口
    final val WEB_SERVER_PORT = 8080

    //服务提供者所在的地址
    final val WEB_SERVER_IP = "127.0.0.1"

    //默认启用cglib
    final val CGLIB_PROXY = true

    //是否强制
    final val FORCE_CGLIB_PROXY = false
  }

}
