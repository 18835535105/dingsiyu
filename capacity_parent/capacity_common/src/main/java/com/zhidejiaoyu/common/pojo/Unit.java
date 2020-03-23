package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/***
 * 单元
 * 
 * @author Administrator
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Unit extends Model<Unit> {
	/** id */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 课程主建 */
    private Long courseId;

    /** 单元名 */
    private String unitName;

    /** 课程单元拼接名 */
    private String jointName;
    
    /** 删除状态 1:未删除（默认），2：删除 */
    @TableField("delStatus")
    private int delStatus;
    
    /** 单元顺序，用于判断当前单元的下一单元是哪个 */
    private Integer unitIndex;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}