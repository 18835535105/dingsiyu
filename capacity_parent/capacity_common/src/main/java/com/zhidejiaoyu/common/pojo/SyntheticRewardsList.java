package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 合成奖励表
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SyntheticRewardsList extends Model<SyntheticRewardsList> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 学生id
     */
    private Integer studentId;
    /**
     * 1,手套，2,花瓣，3,皮肤
     */
    private Integer type;
    private String name;
    /**
     * 奖励图片地址
     */
    private String imgUrl;
    /**
     * 1, 已使用  2,未使用
     */
    private Integer useState;
    /**
     * 使用日期
     */
    private Date useTime;

    /**
     * 到期日期
     */
    private Date useEndTime;

    /**
     * 获取时间
     */
    private Date createTime;
    /**
     * 0为正常合成 1,为惊喜抽取
     */
    private int model;
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
