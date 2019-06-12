package io.growing.dlsrpc.common.utils

import io.growing.dlsrpc.common.exception.ProxyException

/**
 * 获取类实现的接口
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-12
 */
object SuperClassUtils extends App {

  /**
   * 获取类的父接口
   *
   * @param clazz
   * @tparam T
   */
  def getSuperInterfaces[T](clazz: Class[T]): List[String] = {
    val names = clazz.getInterfaces
    names.map(m => m.getSimpleName).toList
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
   * 待优化为枚举
   *
   * @param clazz
   * @tparam T
   * @return
   */
  def matchProxy[T](clazz: Class[T]): String = {
    if (getVaildSuperInterface(clazz) == null || getVaildSuperInterface(clazz).isEmpty) {
      if (Constants.CGLIB_PROXY) {
        "CGLIB"
      } else throw ProxyException("Proxy happen fail")
    } else {
      if (Constants.CGLIB_PROXY && Constants.TO_CGLIB_PROXY) {
        "CGLIB"
      } else {
        "JDK"
      }
    }
  }
}
