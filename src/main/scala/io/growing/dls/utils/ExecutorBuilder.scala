package io.growing.dls.utils

import java.util.concurrent.{ Executor, Executors }

import com.google.common.util.concurrent.ThreadFactoryBuilder

/**
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
object ExecutorBuilder {

  def executorBuild(nameFormat: String, daemon: Boolean): Executor = {
    val threadFactory = new ThreadFactoryBuilder().setDaemon(daemon).setNameFormat(nameFormat).build
    Executors.newCachedThreadPool(threadFactory)
  }
}
