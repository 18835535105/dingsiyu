package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;

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
 * @since 2019-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("EEG_recording")
public class EegRecording extends Model<EegRecording> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 学生id
     */
    @TableField("student_id")
    private Integer studentId;
    /**
     * 1,记忆大挑战 2，乾坤挪移 3，火眼金睛 4，最强大脑
     */
    private Integer type;
    /**
     * 大等级
     */
    @TableField("big_level")
    private Integer bigLevel;
    /**
     * 小等级
     */
    @TableField("small_level")
    private Integer smallLevel;
    /**
     * 答题数
     */
    @TableField("answer_number")
        private Integer answerNumber;
    /**
     * 对题数
     */
    @TableField("pair_number")
    private Integer pairNumber;
    /**
     * 0,未完成 1，完成
     */
    private Integer state;

    /**
     * 连续答对次数集合用string保存
     */
    private String frequency;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 今日最大大等级
     */
    @TableField("level")
    private Integer level;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
