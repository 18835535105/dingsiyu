package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 审批表，跟订单表关联
 * </p>
 *
 * @author zdjy
 * @since 2020-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderItemDetail implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id,uuid
     */
      @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 订单表id
     */
    private String orderItemId;

    /**
     * 学生充值金额
     */
    private Long money;

    /**
     * 审批状态。1：未审批；2：已审批
     */
    private Integer state;

    /**
     * 介绍人
     */
    private Long introduceId;

    /**
     * 介绍人类型。1：学生；2：老师
     */
    private Integer introducType;

    /**
     * 接待老师
     */
    private Long teacherId;

    /**
     * 体验老师
     */
    private Long experienceTeacherId;

    /**
     * 介绍人爱心
     */
    private Integer introduceHeart;

    /**
     * 接待老师爱心
     */
    private Integer teacherHeart;

    /**
     * 体验老师爱心
     */
    private Integer experienceTeacherHeart;

    /**
     * 谈单人爱心
     */
    private Integer businessHeart;

    /**
     * 谈单人id
     */
    private Long businessId;

    /**
     * 审批人id（审批该订单的校长id）
     */
    private Long approvedUserId;

    /**
     * 审批时间
     */
    private LocalDate approvedTime;

    /**
     * 创建时间
     */
    private LocalDate createTime;

    /**
     * 更新时间
     */
    private LocalDate updateTime;


}
