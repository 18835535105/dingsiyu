package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 爱心配置
 * </p>
 *
 * @author zdjy
 * @since 2020-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HeartConfig implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 校区的校长id
     */
    private Integer schoolAdminId;

    /**
     * 是否开启家长爱心。1：开启；2：不开启（默认）
     */
    private Integer openParent;

    /**
     * 是否开启教师爱心。1：开启；2：不开启（默认）
     */
    private Integer openTeacher;

    /**
     * 转介绍人提奖比率，小数百分比
     */
    private Double introducer;

    /**
     * 接待老师提奖比率，小数百分比
     */
    private Double teacher;

    /**
     * 体验老师提奖比率，小数百分比
     */
    private Double experienceTeacher;

    /**
     * 谈单人提成比率，小数百分比
     */
    private Double business;

    /**
     * 金币爱心转化比，比如这里是100，说明转化比是100:1，即100个金币能转化1个爱心
     */
    private Integer goldToHeart;

    /**
     * 课时爱心
     */
    private Integer classHour;

    /**
     * 是否开启提现。1：开启；2：不开启（默认）
     */
    private Integer openWithdrawal;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
