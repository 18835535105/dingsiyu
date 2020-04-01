package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2020-03-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LearningDetails implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 单元id
     */
    private Long unitId;

    /**
     * 课程名
     */
    private String courseName;

    /**
     * 单元名
     */
    private String unitName;

    /**
     * 1,单词 2，句型 3课文 4，字母音标 5，语法
     */
    private Integer type;

    /**
     * group分类
     */
    @TableField("`group`")
    private Integer group;

    /**
     * 学习模块在线时常
     */
    private Long onlineTime;

    /**
     * 有效时常
     */
    private Long validTime;

    /**
     * 1，一键学习，2， 自由学习
     */
    private Integer learningModel;

    /**
     * 学习时间
     */
    private Date studyTime;

    /**
     * 创建时间
     */
    private Date createTime;


}
