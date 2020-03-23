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
import java.util.Date;

/**
 * <p>
 * 阅读课程表
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("read_course")
public class ReadCourse extends Model<ReadCourse> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 课程名（三年级-九月份-第一周）
     */
    @TableField("course_name")
    private String courseName;
    /**
     * 月份（九月份）
     */
    private String month;
    /**
     * 第几周（第一周）
     */
    private String week;
    /**
     * 年级（三年级）
     */
    private String grade;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    @TableField("read_sort")
    private Integer readSort;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
