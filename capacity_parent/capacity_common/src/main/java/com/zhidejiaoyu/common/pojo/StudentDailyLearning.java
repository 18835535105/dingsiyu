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
 * 
 * </p>
 *
 * @author zdjy
 * @since 2020-03-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StudentDailyLearning implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 学习时长
     */
    private Integer validTime;

    /**
     * 金币获取
     */
    private Integer goldAdd;

    /**
     * 金币消费
     */
    private Integer goldConsumption;

    /**
     * 1,打卡 2，未打卡
     */
    private Integer clockIn;

    /**
     * 被加油次数
     */
    private Integer oiling;

    /**
     * 学习时间
     */
    private LocalDateTime studyTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
