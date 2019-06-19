package io.growing.dlsrpc.benchmark;


import io.growing.dlsrpc.benchmark.models.HelloImpl;
import io.growing.dlsrpc.benchmark.models.HelloWorld;
import io.growing.dlsrpc.common.config.DlsRpcConfiguration;
import io.growing.dlsrpc.core.server.ServerBuilder;
import io.growing.dlsrpc.core.utils.DlsRpcInvoke;

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

        //这里的发布不是服务注册，而是实例化暴露bean
        List<Object> tmpList = new ArrayList<>();
        tmpList.add(new HelloWorld());//不要使用我核心包的models，那是测试用的
        tmpList.add(new HelloImpl());
        //演示通过链式调用实例化发布服务
        ServerBuilder server = DlsRpcInvoke.getServerBuilder(DlsRpcConfiguration.WEB_SERVER_PORT(), tmpList);
        //不允许start之前修改端口
        server.build().start();
    }
}
