package io.growing.dlsrpc.common.utils

import com.typesafe.scalalogging.LazyLogging

/**
 * Java 虚拟机关闭钩子
 *
 * 暂时留着
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-16
 */
object ShutdownHook extends LazyLogging {

  @throws[InterruptedException]
  def hook: Unit = {
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      logger.info(this.toString + ",Shutdown executed start")
      try {
        Thread.sleep(3000)
      } catch {
        case e: Exception => {
          logger.warn("ShutdownHook fail because {}", e.getMessage)
        }
      }
      System.exit(0)
      logger.info(this.toString + ",Shutdown executed end ")
    }))
  }
}