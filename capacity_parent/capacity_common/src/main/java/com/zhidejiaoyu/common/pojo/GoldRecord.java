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
public class GoldRecord implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 增加的金币为正数（消耗的金币为负数）
     */
    private Integer gold;

    /**
     * 金币变化说明
     */
    private String content;

    /**
     * 学习时间
     */
    private LocalDateTime studyTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
