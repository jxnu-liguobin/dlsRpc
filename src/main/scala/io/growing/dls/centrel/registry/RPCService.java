package io.growing.dls.centrel.registry;

import java.lang.annotation.*;


/**
 * 标记需要被注册的服务
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RPCService {
}
