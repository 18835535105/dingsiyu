package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 学生扩展内容表；
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-15
 */
@Data
@TableName("student_expansion")
public class StudentExpansion extends Model<StudentExpansion> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("student_id")
    private Long studentId;
    /**
     * 学生学习力
     */
    @TableField("study_power")
    private Integer studyPower;

    @TableField("level")
    private int level;

    @TableField("is_look")
    private Integer isLook;

    /**
     * 背景音乐开关；1：开启；2：关闭
     */
    private Integer audioStatus;

    /**
     * 闯关类当日金币增加量，每天首次登陆时清零
     */
    private Integer testGoldAdd;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
