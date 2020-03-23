package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 超级语法课程表
 * </p>
 *
 * @author zdjy
 * @since 2019-10-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SyntaxCourse extends Model<SyntaxCourse> {

    private static final long serialVersionUID = 1L;

    /**
     * 课程表主建
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 学段
     */
    private String studyParagraph;
    /**
     * 年级
     */
    private String grade;
    /**
     * 版本
     */
    private String version;
    /**
     * 标签
     */
    private String label;
    /**
     * 课程名
     */
    private String courseName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
