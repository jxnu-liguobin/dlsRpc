package io.growing.dlsrpc.common.enums

/**
 * 枚举
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-14
 */
object ProxyType extends Enumeration {

  type ProxyType = Value //声明枚举对外暴露的变量类型

  val CGLIB = Value(-1)

  val JDK = Value(99)

}
