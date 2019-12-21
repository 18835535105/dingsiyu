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
 *
 * </p>
 *
 * @author zdjy
 * @since 2019-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CourseConfig extends Model<CourseConfig> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 校长id
     */
    private Long userId;
    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 可学习模块，1单词2，句型，3课文，4语法
     */
    private String studyModel;
    /**
     * 是否是一键学习：1：一键学习2：非一键学习
     */
    private Integer oneKeyLearn;
    /**
     * 1校区配置 2学生配置
     */
    private Integer type;
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
