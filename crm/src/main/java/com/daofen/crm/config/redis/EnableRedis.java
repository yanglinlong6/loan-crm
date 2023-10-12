package com.daofen.crm.config.redis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(value = {RedisConfig.class})
public @interface EnableRedis {
}
