package io.github.dlsrpc.core.utils

import com.google.inject.Guice
import io.github.dlsrpc.common.utils.CheckCondition
import io.github.dlsrpc.consul.modules.ConsulModule
import io.github.dlsrpc.core.modules.ProviderModule

/**
 * 这里使用guice注入
 *
 * @author 梦境迷离
 * @version 1.1, 2019-06-04
 */
object ServiceLoadUtil {

  //这里把注册和发现都注入了，其实应该分离，除非考虑服务端和客户端是相互为服务
  //但本项目仅考虑单向，无法成为服务提供方的同时又成为服务消费方。
  //即：本项目的服务端只能是提供方，客户端只能是调用方
  private[this] final lazy val inject = Guice.createInjector(new ProviderModule, new ConsulModule)

  def getProvider[T](`type`: Class[T]): T = {
    val instance = inject.getInstance(`type`)
    CheckCondition.conditionException(instance == null, `type`.getSimpleName + " not be found")
    instance
  }
}