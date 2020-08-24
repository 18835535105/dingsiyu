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
 * 爱心流水（爱心操作记录）
 * </p>
 *
 * @author zdjy
 * @since 2020-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class HeartOptRecord implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 老师或者学生id
     */
    private Long userId;

    /**
     * 人员类型。1：学生；2：教师
     */
    private Integer userType;

    /**
     * 收入支出爱心个数。正数为收入，负数为支出
     */
    private Integer heartCount;

    /**
     * 操作事项，记录说明文字
     */
    private String item;

    /**
     * 此处记录学生id，说明该记录是由该学生而产生的
     */
    private Long source;

    /**
     * 记录类别。1：体验老师爱心；2：接待老师爱心；3：课时爱心；4：金币转换；5：介绍爱心；6：谈单人爱心
     */
    private Integer recordType;

    private LocalDateTime createTime;


}
