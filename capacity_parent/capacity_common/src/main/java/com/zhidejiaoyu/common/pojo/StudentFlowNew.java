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
public class StudentFlowNew extends Model<StudentFlowNew> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long studentId;
    /**
     * 当前节点id
     */
    private Long currentFlowId;
    /**
     * learn_new id
     */
    private Long learnId;
    private Date updateTime;

    /**
     * 当前记录所属模块：1、一键排课流程；2、自由学习单词模块流程；3、自由学习句型模块流程；4、自由学习课文模块流程
     */
    private Integer type;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
