package io.growing.dlsrpc.benchmark;


import io.growing.dlsrpc.common.config.DlsRpcConfiguration;
import io.growing.dlsrpc.core.rpctest.HelloImpl;
import io.growing.dlsrpc.core.rpctest.WorldImpl;
import io.growing.dlsrpc.core.server.ServerBuilder;
import io.growing.dlsrpc.core.utils.DlsRpcInvoke;
import scala.collection.JavaConverters;
import scala.collection.Seq;

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
        List<Object> tmpList1 = new ArrayList<>();
        tmpList1.add(new WorldImpl());
        List<Object> tmpList2 = new ArrayList<>();
        tmpList2.add(new HelloImpl());
        Seq<Object> tmpSeq1 = JavaConverters.asScalaIteratorConverter(tmpList1.iterator()).asScala().toSeq();//初始化
        Seq<Object> tmpSeq2 = JavaConverters.asScalaIteratorConverter(tmpList2.iterator()).asScala().toSeq();//追加发布

        //演示通过链式调用实例化发布服务
        ServerBuilder server = DlsRpcInvoke.getServerBuilder(DlsRpcConfiguration.WEB_SERVER_PORT(), tmpSeq1).publishServices(tmpSeq2);
        //不允许start之前修改端口
        server.build().start();
    }
}
