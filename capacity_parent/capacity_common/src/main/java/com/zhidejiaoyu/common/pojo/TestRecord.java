package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 测试记录表
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TestRecord extends Model<TestRecord> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long courseId;

    private Long unitId;

    private Long flowId;

    /**
     * 测试类型（学前游戏测试，等级测试，单元闯关测试，效果检测，阶段测试，复习测试，学后测试, 测试中心(已学测试,生词测试,熟词测试,五维测试)
     */
    private String genre;

    /**
     * 测试开始时间
     */
    private Date testStartTime;

    /**
     * 测试结束时间
     */
    private Date testEndTime;

    /**
     * 得分
     */
    private Integer point;
    /**
     * 历史最高分
     */
    private Integer historyBestPoint;

    /**
     * 历史最低分
     */
    private Integer historyBadPoint;

    /**
     * 题量
     */
    private Integer quantity;

    /**
     * 说明
     */
    private String explain;

    /**
     * 错题数
     */
    private Integer errorCount;

    /**
     * 对题数
     */
    private Integer rightCount;

    /**
     * 学习模块 （单词图鉴，慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     */
    private String studyModel;

    /**
     * 测试奖励金币数
     */
    private Integer awardGold;

    /**
     * 测试成绩大于历史最高分的次数，用于计算当次测试应该奖励的金币个数
     */
    private Integer betterCount;


    /**
     * 针对清学版。目的是不清楚学生的测试记录。1：学习时需要统计的测试；2：学习时不需要统计的测试
     */
    private Integer type;

    /**
     * 1：测试通过；2：测试未通过
     */
    private Integer pass;
    /**
     * 组号
     */
    private Integer group;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
