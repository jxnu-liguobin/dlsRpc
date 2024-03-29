package io.github.dlsrpc.common.utils

import com.typesafe.scalalogging.LazyLogging
import io.github.dlsrpc.common.exception.RpcException

/**
 * 通用判断和输出日志并尽量消除if
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-07
 */
object CheckCondition extends LazyLogging {

  /**
   * 满足判断条件就打印出警告
   *
   * 警告很鸡肋，需要return的地方还是要返回一个值
   *
   * @param condition
   * @param msg 警告信息
   */
  def conditionWarn(condition: => Boolean, msg: String = "warn info"): Boolean = {
    if (condition) {
      logger.warn(msg)
      true
    } else false
  }

  /**
   * 满足条件就抛出通用异常
   *
   * @param condition
   * @param msg   异常说明
   * @param cause 异常原因 默认空
   */
  @throws[RpcException]
  def conditionException(condition: => Boolean, msg: String = "exception info", cause: Throwable = RpcException("default rpc exception")) = {
    if (condition) {
      if (cause != null) {
        logger.error(cause.getMessage)
        throw new RpcException(s"Not satisfying the conditions because : {$msg}", cause)
      }
      throw RpcException(s"Not satisfying the conditions because : {$msg}")
    }
  }
}
