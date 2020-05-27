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
 * 每周活动结果排名
 * </p>
 *
 * @author zdjy
 * @since 2020-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WeekActivityRank implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 活动id
     */
    private Long weekActivityConfigId;

    /**
     * 活动期间完成内容
     */
    private Integer complateCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 昵称
     */
    private String nickName;


}
