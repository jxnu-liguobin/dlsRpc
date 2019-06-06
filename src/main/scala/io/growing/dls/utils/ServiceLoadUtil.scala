package io.growing.dls.utils

import com.google.inject.Guice
import io.growing.dls.exception.RPCException
import io.growing.dls.modules.ProviderModule

import scala.reflect.ClassTag

/**
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
object ServiceLoadUtil {

  lazy val inject = Guice.createInjector(new ProviderModule)

  def getProvider[T: ClassTag](`type`: Class[T]): T = {
    //    for (service <- collection.JavaConverters.asScalaIterator(ServiceLoader.load(`type`).iterator())) {
    //      if (service != null) {
    //        return service
    //      }
    //    }
    val instance = inject.getInstance(`type`)
    if (instance == null) {
      throw RPCException(`type`.getSimpleName + " not be found")
    }
    instance
  }
}