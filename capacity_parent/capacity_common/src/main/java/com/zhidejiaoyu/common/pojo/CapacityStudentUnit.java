package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 智能版学生当前学习课程和单元记录表
 * </p>
 *
 * @author zdjy
 * @since 2018-12-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("all")
public class CapacityStudentUnit extends Model<CapacityStudentUnit> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Long studentId;
    private Long courseId;
    private Long unitId;
    /**
     * 学习模块：1：单词模块；2：例句听力；3：例句默写；4：例句翻译
     */
    private Integer type;

    private String unitName;

    private String courseName;

    private String version;

    /**
     * 学生可学习的起始单元id
     */
    @TableField("startunit")
    private Long startunit;

    /**
     * 学生科学系的结束单元id，当前学习的id等于该id时学生不可再学习下一单元
     */
    @TableField("endunit")
    private Long endunit;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
