package com.zhidejiaoyu.common.Vo.testVo;

import com.zhidejiaoyu.common.pojo.TestRecordInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 学生测试记录详情
 *
 * @author wuchenxi
 * @date 2018/10/20
 */
@Data
public class TestDetailVo implements Serializable {
    /**
     * 题头
     */
    private String title;

    /**
     * 学生姓名
     */
    private String name;

    /**
     * 测试开始时间
     */
    private String testTime;

    /**
     * 测试内容
     */
    private String testContent;

    /**
     * 测试题量
     */
    private String subjectCount;

    /**
     * 答对数量
     */
    private String rightCount;

    /**
     * 答错题量
     */
    private String errCount;

    /**
     * 测试用时
     */
    private String useTime;

    /**
     * 测试得分
     */
    private String score;

    /**
     * 是否是默写模块
     */
    private String isWrite;

    /**
     * 答题详情
     */
    private List<TestRecordInfo> infos;
}
