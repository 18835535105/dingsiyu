package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 学习信息表
 *
 * @author wuchenxi
 * @date 2018年5月10日
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Learn extends Model<Learn> {
    @TableId(type = IdType.AUTO)
	private Long id;

	private Long studentId;

	private Long courseId;

	private Long unitId;

	private Long vocabularyId;

	private Long exampleId;

	/** 词/句生疏情况：0-生词；1-熟词 */
	private Integer status;

	/** 学生当次开始学习该单词/句子的时间 */
	private Date learnTime;

	/** 学习模块 （慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写） */
	private String studyModel;

	/**当前单词/句子学习的次数。当课程学习遍数>1时，该字段作为当前单词或例句是否已学的标识，字段>0说明该字段已学*/
	private Integer studyCount;

	/** 数据更新时间（学习结束时间） */
	private Date updateTime;

	private Integer learnCount;

	/** 第一次学习该课程的时间 (如果该课程是第一次学习，要保存该字段；课程中如果已有该字段则不用重复保存)*/
	private Date firstStudyTime;

    /**
     * 第一次学习该单词是否是熟词，1：熟词；0：生词
     */
	private Integer firstIsKnown;

	/**
	 *1，当前学习  2，以前学习
	 */
	private Integer type;

	/**
	 * 流程名称
	 */
	private String flowName;

	private String courseName;

	/**
	 * 计划中起始单元 id
	 */
	private Long startUnitId;

	/**
	 * 计划中结束单元 id
	 */
	private Long endUnitId;

	/**
	 * 学习计划 id，用于区分当前学习是哪个学习计划
	 */
	private Integer studyPlanId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
