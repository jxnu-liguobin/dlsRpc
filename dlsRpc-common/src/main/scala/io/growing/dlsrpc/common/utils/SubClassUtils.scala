package io.growing.dlsrpc.common.utils

import java.util.{List => JList}

/**
 * 获取接口的实现类
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-18
 */
object SubClassUtils extends App {

  /**
   * 获取接口的实现类的类名
   *
   * @param clazz 接口
   * @tparam T
   * @return 只能获取与该接口同包或者子包
   */
  def getSubClassName[T](clazz: Class[T]): String = {
    val ret: JList[Class[_]] = ClassUtil.getSubClassByInterface(clazz)
    IsCondition.conditionException(ret == null, "this interface don't have any subClass")
    //TODO 多类通过显示指定类名
    IsCondition.conditionException(ret.size() > 1, "this interface have multiple implementation classes")
    ret.get(0).getSimpleName
  }

  /**
   * 获取子类
   *
   * @param clazz
   * @tparam T
   * @return
   */
  def getSubClass[T](clazz: Class[T]) = {
    val ret: JList[Class[_]] = ClassUtil.getSubClassByInterface(clazz)
    IsCondition.conditionException(ret == null || ret.size() == 0, "this interface don't have any subClass")
    //TODO 多类通过显示指定类名
    IsCondition.conditionException(ret.size() > 1, "this interface have multiple implementation classes")
    ret.get(0)
  }
}
