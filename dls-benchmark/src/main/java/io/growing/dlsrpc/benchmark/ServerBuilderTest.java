package io.growing.dlsrpc.benchmark;


import io.growing.dlsrpc.core.rpctest.HelloImpl;
import io.growing.dlsrpc.core.utils.DlsRpcInvoke;


/**
 * 通用服务端 压测和手工均需要启动，根据需要注释另一个
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-12
 */
public class ServerBuilderTest {

    //sbt命令行执行 package 然后把core common consul (非dlsRpc.jar)的jar放到resources下面，替换原来的jar
    //先启动consul，再启动这个server
    public static void main(String[] args) {
        //JDK
        DlsRpcInvoke.publishService(8080, new HelloImpl());
        //CGLIB
//        DlsRpcInvoke.publishService(8083, new WorldImpl());

    }
}
