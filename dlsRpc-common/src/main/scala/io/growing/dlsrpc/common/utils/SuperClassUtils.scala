package io.growing.dlsrpc.common.utils

import io.growing.dlsrpc.common.config.DlsRpcConfiguration._
import io.growing.dlsrpc.common.enums.ProxyType
import io.growing.dlsrpc.common.enums.ProxyType.ProxyType
import io.growing.dlsrpc.common.exception.ProxyException

/**
 * 获取类实现的接口
 *
 * 本类不稳定，可能被废弃
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-12
 */
object SuperClassUtils {

  /**
   * 获取类的父接口
   *
   * @param clazz
   * @tparam T
   */
  @deprecated
  def getSuperInterfaces[T](clazz: Class[T]): List[String] = {
    val names = clazz.getInterfaces
    names.map(m => m.getSimpleName).toList
  }

  /**
   * 如果clazz是interface的实现，则返回clazz
   *
   * @param clazz
   * @param interface
   * @tparam S
   * @tparam B
   * @return
   */
  def CheckSuperInterfaces[S, B](clazz: Class[S], interface: Class[B]): Class[_] = {
    val names = clazz.getInterfaces
    if (names.contains(interface)) {
      clazz
    } else {
      null
    }
  }

  /**
   * 获取类的父接口，排除一些接口
   *
   * @param clazz
   * @param filter
   * @tparam T
   * @return
   */
  def getSuperInterfacesCondition[T](clazz: Class[T], filter: List[String]): List[String] = {
    val names = clazz.getInterfaces
    names.map(m => m.getSimpleName).filter(f => !filter.contains(f)).toList
  }

  /**
   * 获取clazz实现的除Cloneable和Serializable以外的接口
   *
   * @param clazz
   * @tparam T
   */
  def getVaildSuperInterface[T](clazz: Class[T]): List[String] = {
    getSuperInterfacesCondition(clazz, List("Cloneable", "Serializable"))
  }

  /**
   * 匹配代理调用方式
   *
   * @param clazz
   * @tparam T
   * @return
   */
  def matchProxy[T](clazz: Class[T]): ProxyType = {
    //不是接口，且没有实现接口，使用cglib
    if (!clazz.isInterface && (getVaildSuperInterface[T](clazz) == null || getVaildSuperInterface[T](clazz).isEmpty)) {
      if (CGLIB_PROXY) {
        return ProxyType.CGLIB
      } else throw ProxyException("Proxy happen fail")
    }
    //不是接口，有实现接口，使用jdk；是接口使用jdk
    if (clazz.isInterface || !clazz.isInterface && (getVaildSuperInterface[T](clazz) != null && getVaildSuperInterface[T](clazz).nonEmpty)) {
      if (!clazz.isInterface) {
        if (CGLIB_PROXY && TO_CGLIB_PROXY) {
          return ProxyType.CGLIB
        }
      } else {
        return ProxyType.JDK
      }
    }
    ProxyType.JDK
  }
}