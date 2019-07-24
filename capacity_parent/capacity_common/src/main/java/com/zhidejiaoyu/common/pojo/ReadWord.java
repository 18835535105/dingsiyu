package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 阅读生词手册
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ReadWord extends Model<ReadWord> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long studentId;

    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 单词id
     */
    private Long wordId;
    /**
     * 类型 1,慧记忆 2,单词图鉴 3,慧听写 4,慧默写
     */
    private Integer type;
    /**
     * 黄金记忆点
     */
    private Date push;
    /**
     * 记忆强度
     */
    private Double memoryStrength;

    private Date createTime;

    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
