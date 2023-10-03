package org.ashe.security.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 接口调用次数限制
 */
@Target(ElementType.METHOD) // 作用于方法
@Retention(RetentionPolicy.RUNTIME) // 生命周期
public @interface Limit {

    /**
     * 阈值，达到阈值拒绝请求
     */
    long threshold() default 0;

    /**
     * 重置时间，重置时间结束方解除限制
     */
    long reset() default 0;

    /**
     * 重置时间单位，默认分钟，可自定义设置
     */
    TimeUnit unit() default MINUTES;

}