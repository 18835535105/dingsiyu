package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 智能版学习流程数据表
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
public class StudyFlowNew extends Model<StudyFlowNew> {

    private static final long serialVersionUID = 1L;

    /**
     * 流程id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 如果有判断条件，达到判断条件时下个节点id；无判断条件时，下个节点id
     */
    private Integer nextTrueFlow;
    /**
     * 未达到判断条件时下个节点id
     */
    private Integer nextFalseFlow;
    /**
     * 当前节点名称
     */
    private String modelName;
    /**
     * 当前流程名称
     */
    private String flowName;
    /**
     * 90：是否大于或等于90分；80：是否大于或等于80分；3：是否大于等于50分小于80分
     */
    private Integer type;

    @TableField(exist = false)
    private Long courseId;

    @TableField(exist = false)
    private Long unitId;

    @TableField(exist = false)
    private String courseName;

    @TableField(exist = false)
    private String unitName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
