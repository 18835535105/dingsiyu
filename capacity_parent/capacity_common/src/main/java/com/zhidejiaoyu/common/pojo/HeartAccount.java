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
 * 爱心账户
 * </p>
 *
 * @author zdjy
 * @since 2020-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HeartAccount implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 老师或者学生id
     */
    private Long userId;

    /**
     * 人员类型。1：教师；2：学生
     */
    private Integer userType;

    /**
     * 现在还剩余的爱心数
     */
    private Integer reduceHeart;

    /**
     * 总共获取的爱心数
     */
    private Integer totalHeart;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
