package io.growing.dls.utils

import com.google.inject.Guice
import io.growing.dls.modules.ProviderModule

import scala.reflect.ClassTag

/**
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
object ServiceLoadUtil {

  private[this] lazy val inject = Guice.createInjector(new ProviderModule)

  def getProvider[T: ClassTag](`type`: Class[T]): T = {
    val instance = inject.getInstance(`type`)
    IsCondition.conditionException(instance == null, `type`.getSimpleName + " not be found")
    instance
  }
}