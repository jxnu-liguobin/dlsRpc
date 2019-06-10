package io.growing.dlsrpc.core.proxy

import java.lang.reflect.Method

import io.growing.dlsrpc.common.metadata.RpcRequest
import io.growing.dlsrpc.common.utils.IsCondition

/**
 * JDK代理和cgLib代理。需要远程调用，无法抽到到common中，因为common不能依赖core
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-10
 */
class ProxyBuilder[T](obj: T) {

  IsCondition.conditionException(obj == null, "obj can't be null")

  def getProxyObject(): Unit = Some(obj.getClass.getSuperclass) match {
    case Some(subClass) => {
      //有接口使用cglib
    }
    case _ => {
      //TODO 无接口使用jdk代理
    }

  }


//  //执行方法时被调用
//  def invoke(request: RpcRequest, proxy: Any, method: Method, args: Array[_ <: Object]) = {
//    val result: AnyRef = messageHandler.sendProcessor(request)
//    result
//  }


}
