package io.growing.dlsrpc.common.config

import java.util
import java.util.{List => JList}

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

/**
 * 加载配置
 *
 * TODO 后续应当检测，若有配置文件则应该覆盖dlsRpc.conf
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-13
 */
object DlsRpcConfiguration {

  private final val config: Config = ConfigFactory.load("dlsRpc.conf")

  //读取配置文件
  final val CONSUL_ADDRESS_IP: String = Try(config.getString("dlsrpc.consul.host")).getOrElse(Constants.CONSUL_ADDRESS_IP)
  final val CONSUL_ADDRESS_PORT: Int = Try(config.getInt("dlsrpc.consul.port")).getOrElse(Constants.CONSUL_ADDRESS_PORT)
  final val TIME_AWAIT: Int = Try(config.getInt("dlsrpc.client.timeout")).getOrElse(Constants.TIME_AWAIT)
  final val REQUEST_START_VALUE: Int = Try(config.getInt("dlsrpc.client.request-start-value")).getOrElse(Constants.REQUEST_START_VALUE)
  final val MESSAGE_LENGTH: Int = Try(config.getInt(" dlsrpc.http.message-length")).getOrElse(Constants.MESSAGE_LENGTH)
  final val PATTERN: String = Try(config.getString("dlsrpc.consul.adress-pattern")).getOrElse(Constants.PATTERN)
  final val IP_PATTERN: String = Try(config.getString("dlsrpc.consul.ip-pattern")).getOrElse(Constants.IP_PATTERN)
  final val PACKAGE_SERVICE: String = Try(config.getString("dlsrpc.consul.registry.package-service")).getOrElse(Constants.PACKAGE_SERVICE)
  final val DEFAULT_DISCOVER_ADDRESS: String = Try(config.getString("dlsrpc.server.address.default")).getOrElse(Constants.DEFAULT_DISCOVER_ADDRESS)
  final val CGLIB_PROXY: Boolean = Try(config.getBoolean("dlsrpc.proxy.mode.cglib-proxy")).getOrElse(Constants.CGLIB_PROXY)
  final val TO_CGLIB_PROXY: Boolean = Try(config.getBoolean("dlsrpc.proxy.mode.force-cglib-proxy")).getOrElse(Constants.TO_CGLIB_PROXY)
  final val WEB_SERVER_PORT: Int = Try(config.getInt("dlsrpc.server.port")).getOrElse(Constants.WEB_SERVER_PORT)

  private final lazy val default: JList[String] = new util.ArrayList[String]()
  default.add(DEFAULT_DISCOVER_ADDRESS)

  final val SERVICE_IP_LIST: JList[String] = Try(config.getStringList("dlsrpc.consul.server.address")).getOrElse(default)

  //默认配置，外界不可访问
  private object Constants {

    final val WEB_SERVER_PORT = 8080

    //客户端超时时间
    final val TIME_AWAIT = 30 * 1000

    //请求id，一般从 0 开始自增
    final val REQUEST_START_VALUE = 0

    final val MESSAGE_LENGTH = 4

    final val PATTERN = "(?:(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5]):\\d{0,5}"

    final val IP_PATTERN = "(?:(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5])"

    //默认注册的包下的service
    final val PACKAGE_SERVICE = "io.growing.dlsrpc"

    //服务发现与注册中心，默认ip
    final val CONSUL_ADDRESS_IP = "127.0.0.1"

    //默认端口
    final val CONSUL_ADDRESS_PORT = 8500

    //默认是服务调用地址就是本地，暂时不用
    final val DEFAULT_DISCOVER_ADDRESS = "localhost:8080"

    //默认启用cglib
    final val CGLIB_PROXY = true

    //是否强制
    final val TO_CGLIB_PROXY = false
  }

}
