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
 * 订单表
 * </p>
 *
 * @author zdjy
 * @since 2020-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderItem implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id,uuid
     */
      @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 学生id
     */
    private Long userId;

    /**
     * 学生充值金额
     */
    private Long money;

    /**
     * 审批状态。1：未审批；2：已审批
     */
    private Integer state;

    /**
     * 订单开始日期
     */
    private LocalDate startTime;

    /**
     * 订单结束日期
     */
    private LocalDate endTime;

    /**
     * 创建时间
     */
    private LocalDate createTime;

    /**
     * 更新时间
     */
    private LocalDate updateTime;


}
