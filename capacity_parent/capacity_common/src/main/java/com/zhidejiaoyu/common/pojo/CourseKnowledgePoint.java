package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 知识点课程-知识点关联表
 * </p>
 *
 * @author zdjy
 * @since 2019-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CourseKnowledgePoint extends Model<CourseKnowledgePoint> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 知识点id
     */
    private Long knowledgePointId;
    /**
     * 知识点课程id
     */
    private Long courseId;
    /**
     * 课程类型：1.单词课程；2.句型课程；3.课文课程；4.超级语法课程
     */
    private Integer type;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
