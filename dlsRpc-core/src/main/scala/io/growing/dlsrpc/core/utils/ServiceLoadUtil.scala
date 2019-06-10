package io.growing.dlsrpc.core.utils

import com.google.inject.Guice
import io.growing.dlsrpc.common.utils.IsCondition
import io.growing.dlsrpc.core.modules.ProviderModule

/**
 * 这里使用guice注入
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
object ServiceLoadUtil {

  private[this] lazy val inject = Guice.createInjector(new ProviderModule)

  def getProvider[T](`type`: Class[T]): T = {
    val instance = inject.getInstance(`type`)
    IsCondition.conditionException(instance == null, `type`.getSimpleName + " not be found")
    instance
  }
}