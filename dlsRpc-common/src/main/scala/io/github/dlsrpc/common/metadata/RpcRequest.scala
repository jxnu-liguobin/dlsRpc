package io.github.dlsrpc.common.metadata

import scala.beans.BeanProperty

/**
 * 请求
 *
 * 兼容Java
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
sealed case class RpcRequest() {

  //请求id，线程安全
  @BeanProperty
  var requestId: Long = _
  //调用的服务名称
  @BeanProperty
  var beanClass: Class[_] = _
  //调用的方法名称
  @BeanProperty
  var methodName: String = _
  //调用版本
  @BeanProperty
  var version: String = _
  //参数类型列表
  @BeanProperty
  var parameterTypes: Array[Class[_]] = _
  //参数列表
  @BeanProperty
  var parameters: Array[_] = _

  def this(requestId: Long, beanClass: Class[_], methodName: String, version: String,
           parameterTypes: Array[Class[_]], parameters: Array[_]) {
    this()
    this.requestId = requestId
    this.beanClass = beanClass
    this.methodName = methodName
    this.version = version
    this.parameterTypes = parameterTypes
    this.parameters = parameters
  }
}