package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 阅读判断对错表
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("read_judge")
public class ReadJudge extends Model<ReadJudge> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 课程id（队长将阅读使用）
     */
    @TableField("course_id")
    private Long courseId;
    /**
     * 阅读类型表id
     */
    @TableField("read_type_id")
    private Long readTypeId;
    /**
     * 题目
     */
    private String subject;
    /**
     * 答案
     */
    private String answer;
    /**
     * 解析
     */
    private String analysis;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
