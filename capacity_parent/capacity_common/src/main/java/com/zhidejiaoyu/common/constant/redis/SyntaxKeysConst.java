package com.zhidejiaoyu.common.constant.redis;

/**
 * 语法缓存key常量值
 *
 * @author: wuchenxi
 * @Date: 2019/10/31 10:14
 */
public interface SyntaxKeysConst {

    /**
     * 语法单元中语法学习内容个数
     * SYNTAX_CONTENT_COUNT_WITH_UNIT + unitId + ：+ 学习模块
     */
    String SYNTAX_CONTENT_COUNT_WITH_UNIT = "SYNTAX_COUNT_WITH_UNIT:";

    /**
     * 当前单元下语法知识点个数
     * SYNTAX_CONTENT_COUNT_WITH_UNIT + unitId
     */
    String KNOWLEDGE_POINT_COUNT_WITH_UNIT = "KNOWLEDGE_POINT_COUNT_WITH_UNIT:";
}
