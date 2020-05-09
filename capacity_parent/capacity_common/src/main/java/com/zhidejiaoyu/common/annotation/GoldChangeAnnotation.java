package com.zhidejiaoyu.common.annotation;

import java.lang.annotation.*;

/**
 * 金币
 *
 * @author wuchenxi
 * @date 2019-04-01
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GoldChangeAnnotation {
}
