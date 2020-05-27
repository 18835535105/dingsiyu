package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 运营每周活动配置
 * </p>
 *
 * @author zdjy
 * @since 2020-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WeekActivityConfig implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 每周活动id
     */
    private Long weekActivityId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动时间
     */
    private String activityDateStr;

    /**
     * 活动开始时间
     */
    private LocalDateTime activityDateBegin;

    /**
     * 活动结束时间
     */
    private LocalDateTime activityDateEnd;


}
