package com.zhidejiaoyu.common.vo.student.testCenter;

import lombok.Data;

import java.io.Serializable;

/**
 * 测试中心页数据展示
 *
 * @author wuchenxi
 * @date 2018-12-13
 */
@Data
public class TestCenterVo implements Serializable {

    /**
     * 0:单词图鉴 1:慧记忆 2:慧听写 3:慧默写 4:例句翻译 5:例句听力 6:例句默写
     */
    private Integer classify;

    /**
     * 已学测试个数
     */
    private Integer alreadyStudy;

    /**
     * 生词测试个数
     */
    private Integer accrue;

    /**
     * 熟词测试个数
     */
    private Integer ripe;
}
