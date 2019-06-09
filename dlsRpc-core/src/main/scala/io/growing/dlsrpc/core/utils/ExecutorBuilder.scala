package io.growing.dlsrpc.core.utils

import java.util.concurrent.{Executor, Executors, ThreadFactory}

import com.google.common.util.concurrent.ThreadFactoryBuilder

/**
 * 线程池
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-04
 */
object ExecutorBuilder {

  def executorBuild(nameFormat: String, daemon: Boolean): Executor = {
    val threadFactory: ThreadFactory = new ThreadFactoryBuilder().setDaemon(daemon).setNameFormat(nameFormat).build
    Executors.newCachedThreadPool(threadFactory)
  }
}
