package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 时长表,用于记录学生在线时长和学习有效时长
 * 登录时间：
 * 学生登录时将当前登录时间记录到session中
 * 有效时长：
 * 学生每次进入学习页面开始记录有效时长直至学生从学习页面离开（前端先存储到本地，为本次登录之后所有有效时长相加，待学生退出登录后，传给服务器端）
 * 在线时长：
 * 记录学生从登录到系统直至退出系统之间的时间
 *
 * @author wuchenxi
 * @date 2018年5月10日
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Duration extends Model<Duration> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long courseId;

    private Long unitId;

    /**
     * 学生退出时由前端发送给服务器记录,学生本次登录期间学习中所有有效时长总和,单位：秒(s)
     */
    private Long validTime;

    /**
     * 学生从登录到退出的在线时长
     */
    private Long onlineTime;

    /**
     * 学生登录系统时间
     */
    private Date loginTime;

    /**
     * 学生退出登录时间
     */
    private Date loginOutTime;

    /**
     * 学习模块，区分各个学习模块的时长，1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写
     */
    private Integer studyModel;

    /**
     * 学习计划的学习遍数
     */
    private Integer studyCount;

    /**
     * 学习计划 id，用于区分当前时长信息是属于哪个学习计划的
     */
    private Integer studyPlanId;
    /**
     * 学习模式 1，自由学习 2，一键学习
     */
    private Integer learningModel;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
