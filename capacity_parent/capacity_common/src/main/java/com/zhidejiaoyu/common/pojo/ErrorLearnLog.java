package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
    @TableField("`group`")
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
    /**
     * 学习id
     */
    private Long wordId;

    /**
     * 当前记录被复习的次数，每复习一次+1，默认为1
     */
    private Integer reviewCount;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
