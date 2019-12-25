package com.zhidejiaoyu.common.pojo;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ErrorLearnLog extends Model<ErrorLearnLog> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 学生id
     */
    private Long studentId;
    private Long unitId;
    /**
     * 学习类型：1单词；2句型；3语法；4课文
     */
    private Integer type;
    /**
     * 学习模块
     */
    private String studyModel;
    /**
     * 难易类型：1：简单类型；2：难类型
     */
    private Integer easyOrHard;
    /**
     * 分组序号
     */
    private Integer group;
    /**
     * 原题
     */
    private String topic;
    /**
     * 错误答案
     */
    private String errorAnswer;
    /**
     * 学习记录更新时间
     */
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
