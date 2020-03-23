package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 学生当前所学课程单元 - 清学智能版
 * </p>
 *
 * @author zdjy
 * @since 2018-09-07
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SimpleStudentUnit extends Model<SimpleStudentUnit> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 课程表id
     */
    private Long courseId;
    /**
     * 单元id
     */
    private Long unitId;
    /**
     * 1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;
     */
    private Integer type;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
