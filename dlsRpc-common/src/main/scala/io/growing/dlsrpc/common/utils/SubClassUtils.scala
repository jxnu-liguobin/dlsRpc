package io.growing.dlsrpc.common.utils

import java.util.{List => JList}

/**
 * 获取接口的实现类
 *
 * 本类不稳定，可能被废弃
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-18
 */
@deprecated
object SubClassUtils {

  /**
   * 获取接口的实现类的类名
   *
   * @param clazz 接口
   * @tparam T
   * @return 只能获取与该接口同包或者子包
   */
  def getSubClassName[T](clazz: Class[T]): String = {
    val ret: JList[Class[_]] = ClassUtil.getSubClassByInterface(clazz)
    CheckCondition.conditionException(ret == null, "this interface don't have any subClass")
    //TODO 多类通过显示指定类名
    CheckCondition.conditionException(ret.size() > 1, "this interface have multiple implementation classes")
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
    CheckCondition.conditionException(ret == null || ret.size() == 0, "this interface don't have any subClass")
    //TODO 多类通过显示指定类名
    CheckCondition.conditionException(ret.size() > 1, "this interface have multiple implementation classes")
    ret.get(0)
  }
}
