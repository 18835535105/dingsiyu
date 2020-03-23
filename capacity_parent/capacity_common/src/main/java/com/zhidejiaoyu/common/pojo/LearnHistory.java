package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LearnHistory extends Model<LearnHistory> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 学生id
     */
    private Long studentId;
    private Long courseId;
    /**
     * 单元id
     */
    private Long unitId;
    /**
     * 分组序号
     */
    @TableField("`group`")
    private Integer group;
    /**
     * 学习类别1：单词；2：句型；3：语法；4：课文
     */
    private Integer type;
    /**
     * 难易类型1：简单类型；2：难类型
     */
    private Integer easyOrHard;
    /**
     * 当前单元学习变数
     */
    private Integer studyCount;
    /**
     * 学习记录更新时间
     */
    private Date updateTime;
    /**
     * 1正在学习，2已学完
     */
    private Integer state;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
