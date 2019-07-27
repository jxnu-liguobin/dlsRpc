package io.github.dlsrpc.consul.registry;

import java.lang.annotation.*;


/**
 * 标记需要被注册的服务
 * <p>
 * 1.对于无实现接口的类，该注解在类上
 * 2.对于有实现接口的类，该注解在接口上。若该接口无实现类并且使用了该注解将抛出异常
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RPCService {
}
