package com.zhidejiaoyu.common.annotation;

import java.lang.annotation.*;

/**
 * 当保存测试记录时判断是否有达到条件的奖励
 *
 * @author wuchenxi
 * @date 2019-04-01
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TestChangeAnnotation {
    /**
     * 如果当前测试跟单元闯关测试相关奖励有关，
     *
     * <ul>
     *     <li>true：当前测试跟单元闯关测试相关奖励有关；比如‘闯关成功10个单元闯关测试’</li>
     *     <li>false：当前测试跟所有测试相关奖励有关；比如"最有潜力勋章'奖励</li>
     * </ul>
     *
     * 该属性为 true，否则为 false
     *
     * @return
     */
    boolean isUnitTest() default true;
}
