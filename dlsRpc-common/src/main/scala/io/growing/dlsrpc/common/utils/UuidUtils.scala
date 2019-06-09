package io.growing.dlsrpc.common.utils

/**
 * 服务注册的id，必须是唯一
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-08
 */
object UuidUtils {

  import java.util.UUID

  def getUid: String = UUID.randomUUID.toString.replaceAll("-", "")

}
