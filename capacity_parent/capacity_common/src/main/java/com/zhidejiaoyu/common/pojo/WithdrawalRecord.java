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
 * 提现记录
 * </p>
 *
 * @author zdjy
 * @since 2020-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WithdrawalRecord implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id,uuid
     */
      @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 提现的人员id
     */
    private Long userId;

    /**
     * 1：学生提现；2：教师提现
     */
    private Integer type;

    /**
     * 提现金额
     */
    private Integer money;

    /**
     * 操作人id，校长id
     */
    private Long optUserId;

    /**
     * 创建时间
     */
    private LocalDate createTime;

    /**
     * 更新时间
     */
    private LocalDate updateTime;


}
