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
 * 学生复习记录表，记录学生复习数据
 * </p>
 *
 * @author zdjy
 * @since 2019-01-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StudentRestudy extends Model<StudentRestudy> {

    private static final long serialVersionUID = 1L;

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
     * 单词/词组/句型id
     */
    private Long vocabularyId;
    /**
     * 单词/词组/句型
     */
    private String word;
    /**
     * 学生复习时间
     */
    private Date updateTime;
    /**
     * 1:单词；2：句型
     */
    private Integer type;
    /**
     * 1：提分版（清学版）；2：同步版（智能版）
     */
    private Integer version;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
