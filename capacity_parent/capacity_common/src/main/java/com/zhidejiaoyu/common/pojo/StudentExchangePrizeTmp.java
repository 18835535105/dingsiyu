package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 迁移数据时，用于临时存放学生兑奖记录，然后通过奖品名称将这些记录放到兑奖记录正式表中
 * </p>
 *
 * @author zdjy
 * @since 2020-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StudentExchangePrizeTmp implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 奖品名称
     */
    private String prizeName;

    /**
     * 学生列表id
     */
    private Long studentId;

    /**
     * 学生兑换时间
     */
    private Date createTime;

    /**
     * 状态 1,未删除   2，已删除
     */
    private Integer state;


}
