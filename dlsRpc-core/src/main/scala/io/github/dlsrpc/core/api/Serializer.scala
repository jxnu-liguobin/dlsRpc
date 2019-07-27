package io.github.dlsrpc.core.api

/**
 * 序列化顶级接口
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
trait Serializer {

  /**
   * 序列化
   *
   * @param obj
   * @tparam T
   * @return
   */
  def serializer[T](obj: T): Array[Byte]

  /**
   * 反序列化
   *
   * @param data
   * @param clazz
   * @tparam T
   * @return
   */
  def deserializer[T](data: Array[Byte], clazz: Class[T]): T
}
