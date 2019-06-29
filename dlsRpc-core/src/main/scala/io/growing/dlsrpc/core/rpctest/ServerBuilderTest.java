package io.growing.dlsrpc.core.rpctest;


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
        //默认从consul获得的服务地址是8080,配置 dlsrpc.server.address.default = "127.0.0.1:8080"
        //也就是客户端获取服务实际地址后会向127.0.0.1:8081发起请求
        //服务启动端口也是8081，Server start port : 8081 如果启动马上结束一般是端口被占用
        ServerBuilder server = DlsRpc.getServerBuilder(Configuration.WEB_SERVER_PORT(), tmpList1);
        server.build().start();
    }
}
