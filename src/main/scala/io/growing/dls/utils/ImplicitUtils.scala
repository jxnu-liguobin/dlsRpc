package io.growing.dls.utils

import java.util.{Iterator => JIterator}

import scala.collection.JavaConverters

/**
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
object ImplicitUtils {


  /**
   * 将Java对象的迭代器转化为Scala的迭代器，不然无法使用Scala的for推断
   *
   * @param it
   * @tparam T
   * @return
   */
  implicit def javaItToScalaIt[T](it: JIterator[T]): Iterator[T] = {
    JavaConverters.asScalaIterator[T](it)
  }
}
