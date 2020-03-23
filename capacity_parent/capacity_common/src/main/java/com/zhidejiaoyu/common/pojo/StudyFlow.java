package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class StudyFlow extends Model<StudyFlow> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer nextTrueFlow;

    private Integer nextFalseFlow;

    private String modelName;

    private String flowName;

    private Integer type;

    @TableField(exist = false)
    private Long courseId;

    @TableField(exist = false)
    private Long unitId;

    @TableField(exist = false)
    private String courseName;

    @TableField(exist = false)
    private String unitName;
    
    /** true代表是新生, 需要走独立的奖励规则*/
    //@TableField(exist = false)
    //private boolean neogenesis;
    
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}