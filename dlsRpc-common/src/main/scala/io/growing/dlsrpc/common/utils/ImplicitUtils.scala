package io.growing.dlsrpc.common.utils

import java.util.{Iterator => JIterator, List => JList, Map => JMap}

import scala.collection.{JavaConverters, Map => SMap}

/**
 * @author 梦境迷离
 * @version 1.1, 2019-06-08
 */
object ImplicitUtils {

  /**
   * 将 Java 对象的迭代器转化为 Scala 的迭代器，不然无法使用Scala的for推断
   *
   * 使用下面的，废弃这个
   *
   * @param it
   * @tparam T
   * @return
   */
  @deprecated
  implicit def jIteratorToSIterator[T](it: JIterator[T]): Iterator[T] = {
    JavaConverters.asScalaIterator[T](it)
  }

  /**
   * 将 Java List转换为一个不可变的SET
   *
   * @param jList
   * @tparam A
   * @return
   */
  implicit def jListToSSet[A](jList: JList[A]): Set[A] = {
    JavaConverters.asScalaBuffer[A](jList).toSet
  }

  /**
   * Java Map 转换为 Scala Map 以使用函数式操作
   *
   * @param cMap
   * @tparam A
   * @tparam B
   * @return
   */
  implicit def jMapToSMap[A, B](cMap: JMap[A, B]): SMap[A, B] = {
    JavaConverters.mapAsScalaMap(cMap)
  }

  /**
   * Scala Map 转换为 Java Map
   *
   * @param sMap
   * @tparam A
   * @tparam B
   * @return
   */
  implicit def sMapToJMap[A, B](sMap: SMap[A, B]): JMap[A, B] = {
    JavaConverters.mapAsJavaMap(sMap)
  }

  /**
   * 兼容Java API 需要使用Seq[Any]，不能使用List<Object>
   *
   * @param list
   * @tparam A
   * @return
   */
  implicit def jListToSeq[A](list: JList[A]): Seq[A] = {
    JavaConverters.asScalaIteratorConverter(list.iterator).asScala.toSeq
  }
}
