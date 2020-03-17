package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 挑战书表；
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Gauntlet extends Model<Gauntlet> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 发起挑战的学生 id
     */
    @TableField("challenger_student_id")
    private Long challengerStudentId;
    /**
     * 被挑战的学生 id
     */
    @TableField("be_challenger_student_id")
    private Long beChallengerStudentId;
    /**
     * 挑战的课程 id
     */
    @TableField("course_id")
    private Long courseId;
    /**
     * 押注的金币数
     */
    @TableField("bet_gold")
    private Integer betGold;
    /**
     * 挑战方式名称
     */
    @TableField("challenge_name")
    private String challengeName;
    /**
     * 发起挑战的学生得分
     */
    @TableField("challenger_point")
    private Integer challengerPoint;
    /**
     * 被挑战的学生得分
     */
    @TableField("be_challenger_point")
    private Integer beChallengerPoint;
    /**
     * 挑战留言
     */
    @TableField("challenger_msg")
    private String challengerMsg;
    /**
     * 发起挑战的学生挑战书状态：1：胜利；2：失败；3：等待；4：超时
     */
    @TableField("challenge_status")
    private Integer challengeStatus;
    /**
     * 被挑战的学生挑战书状态：1：胜利；2：失败；3：等待；4：超时
     */
    @TableField("be_challenger_status")
    private Integer beChallengerStatus;
    /**
     * 发起挑战的学生学习力变化值
     */
    @TableField("challenge_study")
    private Integer challengeStudy;
    /**
     * 被挑战的学生学习力变化值
     */
    @TableField("be_challenge_study")
    private Integer beChallengeStudy;
    /**
     * 发起挑战的学生金币变化值
     */
    @TableField("challenge_gold")
    private Integer challengeGold;
    /**
     * 被挑战的学生金币变化值
     */
    @TableField("be_challenge_gold")
    private Integer beChallengeGold;

    @TableField("concede")
    private String concede;

    @TableField("create_time")
    private Date createTime;

    @TableField("challenger_study_now")
    private Integer challengerStudyNow;

    @TableField("be_challenger_study_now")
    private Integer beChallengerStudyNow;

    @TableField("grade")
    private Integer grade;

    @TableField("award")
    private Integer award;

    @TableField("challenger_img_url")
    private Integer challengerImgUrl;

    @TableField("be_challenger_img_url")
    private Integer beChallengerImgUrl;

    @TableField("type")
    private Integer type;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
