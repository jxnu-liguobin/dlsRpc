package io.growing.dlsrpc.core.serialize

import java.util.concurrent.{Callable, ExecutionException, TimeUnit}

import com.dyuproject.protostuff.Schema
import com.dyuproject.protostuff.runtime.RuntimeSchema
import com.google.common.cache.{Cache, CacheBuilder}
import io.growing.dlsrpc.common.exception.RPCException

/**
 * Schema缓存
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-05
 */
class SchemaCache {

  private[this] final lazy val cache: Cache[Class[_], Schema[_]] = CacheBuilder.newBuilder.maximumSize(1024).expireAfterWrite(1,
    TimeUnit.HOURS).build[Class[_], Schema[_]]

  private[this] def get(cls: Class[_], cache: Cache[Class[_], Schema[_]]) = {
    try {
      cache.get(cls, new Callable[RuntimeSchema[_]]() {
        @throws[Exception]
        override def call: RuntimeSchema[_] = RuntimeSchema.createFrom(cls)
      })
    }
    catch {
      case e: ExecutionException =>
        throw new RPCException("Get Schema fail : {}", e)
    }
  }

  def get(cls: Class[_]): Schema[_] = get(cls, cache)
}

object SchemaCache {

  private[this] lazy val myCache = new SchemaCache

  private[this] def getCache: SchemaCache = myCache

  def getInstance: SchemaCache = getCache

}