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
 * 简版记忆追踪表
 * </p>
 *
 * @author zdjy
 * @since 2018-09-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SimpleCapacity extends Model<SimpleCapacity> {

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
     * 单词/词组/句型中文意思
     */
    private String wordChinese;
    /**
     * 答错次数
     */
    private Integer faultTime;
    /**
     * 黄金记忆点时间
     */
    private Date push;
    /**
     * 记忆强度
     */
    private Double memoryStrength;
    /**
     * 1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;
     */
    private Integer type;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
