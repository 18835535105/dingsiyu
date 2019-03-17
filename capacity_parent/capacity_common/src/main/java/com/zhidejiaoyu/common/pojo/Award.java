package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class Award extends Model<Award> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    /**
     * 勋章信息id
     */
    private Long medalType;

    /**
     * 奖励内容类型
     */
    private Integer awardContentType;

    /**
     * 是否可领取：1：可领取；2：不可领取
     */
    private Integer canGet;

    /**
     * 奖励类型 1:日奖励；2：任务奖励；3：勋章
     */
    private Integer type;

    /**
     * 领取状态：1，已领取；2，未领取
     */
    private Integer getFlag;

    /**
     * 奖励生成时间
     */
    private Date createTime;

    private Date getTime;


    /**
     * 当前完成进度
     */
    private Integer currentPlan;

    /**
     * 需要完成的总进度
     */
    private Integer totalPlan;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}