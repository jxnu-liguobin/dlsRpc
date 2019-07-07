package io.growing.dlsrpc.test;


import io.growing.dlsrpc.common.config.Configuration;
import io.growing.dlsrpc.core.DlsRpc;
import io.growing.dlsrpc.core.server.ServerBuilder;

import java.util.ArrayList;
import java.util.List;


/**
 * 通用服务端 压测和手工均需要启动
 *
 * @author 梦境迷离
 * @version 1.0, 2019-06-12
 */
public class ServerBuilderTest {

    //先启动consul，再启动这个server
    public static void main(String[] args) {
        List<Object> tmpList1 = new ArrayList<>();
        //暴露三个服务
        tmpList1.add(new HelloWorld());
        tmpList1.add(new HelloImpl());
        tmpList1.add(new WorldImpl());
        //服务端如果启动马上结束一般是端口被占用
        //不开启consul，则使用默认地址localhost:8080 ，此端口与8080需要相同。否则需要开启consul，并配置dlsrpc.server.port、dlsrpc.server.ip
        ServerBuilder server = DlsRpc.getServerBuilder(Configuration.WEB_SERVER_PORT(), tmpList1);
        server.build().start();
    }
}
