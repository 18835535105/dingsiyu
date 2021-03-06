package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

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
@EqualsAndHashCode(callSuper = false)
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

    @TableField("pk_explain")
    private Integer pkExplain;

    private String phase;

    /**
     * 背景音乐开关；1：开启；2：关闭
     */
    private Integer audioStatus;

    /**
     * 闯关类当日金币增加量，每天首次登陆时清零
     */
    private Integer testGoldAdd;

    /**
     * 学生达到全校前 3 名时的时间，用于判断“拔得头筹”勋章
     */
    private Date betterThreeTime;

    /**
     * 引导页
     */
    private String guide;

    /**
     * 飞船配置需要展示的勋章id
     */
    private String medalNo;

    /**
     * 源分战力值
     */
    private Integer sourcePower;

    /**
     * 最大排行
     */
    private Long ranking;

    /**
     * 代金券数量
     */
    private Integer cashCoupon;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
