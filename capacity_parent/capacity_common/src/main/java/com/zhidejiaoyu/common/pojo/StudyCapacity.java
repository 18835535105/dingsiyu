package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 黄金记忆点
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
public class StudyCapacity extends Model<StudyCapacity> {

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
     * 单词，句型，字母，音标，知识点内容id
     */
    private Long wordId;
    /**
     * 单词
     */
    private String word;
    /**
     * 单词音标
     */
    private String syllable;
    /**
     * 中文翻译
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
     * 类型：20，读语法 21，选语法 22，写语法
     */
    private Integer type;
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
