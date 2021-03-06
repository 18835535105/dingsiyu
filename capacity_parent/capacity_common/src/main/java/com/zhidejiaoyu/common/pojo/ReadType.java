package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 阅读类型表
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("read_type")
public class ReadType extends Model<ReadType> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 短文类型
     */
    @TableField("types_of_essays")
    private String typesOfEssays;
    /**
     * 难度
     */
    private Integer difficulty;
    /**
     * 课程id
     */
    @TableField("course_id")
    private Long courseId;
    /**
     * 词量
     */
    @TableField("word_quantity")
    private String wordQuantity;
    /**
     * 建议学习时间
     */
    @TableField("learn_time")
    private String learnTime;
    /**
     * 阅读名称
     */
    @TableField("read_name")
    private String readName;
    /**
     *测试类型
     */
    @TableField("test_type")
    private int testType;
    /**
     * 题目数量
     */
    @TableField("read_count")
    private int readCount;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
