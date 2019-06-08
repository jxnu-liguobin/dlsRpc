package io.growing.dls.utils

/**
 * 系统常量
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-07
 */
object Constants {

  //客户端超时时间
  final lazy val TIME_AWAIT: Int = 30 * 1000

  //请求id，一般从 0 开始自增
  final lazy val REQUEST_START_VALUE: Int = 0

  final lazy val MESSAGE_LENGTH: Int = 4

  final lazy val PATTERN = "(?:(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}(?:[0,1]?\\d?\\d|2[0-4]\\d|25[0-5]):\\d{0,5}"

  //默认注册的包下的service
  final lazy val PACKAGE_SERVICE = "io.growing.dls"

  //服务发现与注册中心
  final lazy val CONSUL_ADDRESS = "127.0.0.1:8500"

  //默认是服务调用地址就是本地，暂时不用
  final lazy val DEFAULT_DISCOVER_ADDRESS = "localhost:8080"


}
