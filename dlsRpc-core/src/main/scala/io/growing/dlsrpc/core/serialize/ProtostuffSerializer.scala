package io.growing.dlsrpc.core.serialize

import com.dyuproject.protostuff.runtime.RuntimeSchema.getSchema
import com.dyuproject.protostuff.{LinkedBuffer, ProtostuffIOUtil, Schema}
import com.typesafe.scalalogging.LazyLogging
import io.growing.dlsrpc.common.exception.RpcException
import io.growing.dlsrpc.core.api.Serializer

/**
 * 序列化实现使用Protostuff
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
class ProtostuffSerializer extends Serializer with LazyLogging {

  override def serializer[T](obj: T): Array[Byte] = {
    val buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE)
    try {
      val schema: Schema[T] = SchemaCache.getInstance.get(obj.getClass).asInstanceOf[Schema[T]]
      ProtostuffIOUtil.toByteArray(obj, schema, buffer)
    } catch {
      case e: Exception =>
        logger.warn("Protostuff serializer fail  obj : {}  because : {}", obj, e)
        throw new RpcException("Protostuff serializer fail : {}", e)
    } finally buffer.clear
  }

  override def deserializer[T](data: Array[Byte], clazz: Class[T]): T = {
    try {
      val obj: T = clazz.newInstance
      val schema: Schema[T] = getSchema(clazz)
      ProtostuffIOUtil.mergeFrom(data, obj, schema)
      obj
    } catch {
      case e: Exception =>
        logger.warn("Protostuff deserializer fail  data : {} because : {}", data, e)
        throw new RpcException("Protostuff deserializer fail : {}", e)
    }
  }
}
