package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
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