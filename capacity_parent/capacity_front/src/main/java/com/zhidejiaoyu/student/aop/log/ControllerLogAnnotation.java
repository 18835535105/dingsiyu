package com.zhidejiaoyu.student.aop.log;

import java.lang.annotation.*;

/**
 * 打印控制层日志
 *
 * @author wuchenxi
 * @date 2019-05-16
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ControllerLogAnnotation {

    /**
     * 接口名称
     *
     * @return
     */
    String name() default "";
}
