package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
