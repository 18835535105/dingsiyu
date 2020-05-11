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
 * 学生每周解锁时常
 * </p>
 *
 * @author zdjy
 * @since 2020-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WeekHistoryPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 时常
     */
    private Long onlineTime;

    /**
     * 有效时常
     */
    private Long validTime;

    /**
     * 单词数
     */
    private Integer word;

    /**
     * 分数
     */
    private Integer point;

    /**
     * 每周开始时间
     */
    private Date startTime;

    /**
     * 每周结束时间
     */
    private Date endTime;


}
