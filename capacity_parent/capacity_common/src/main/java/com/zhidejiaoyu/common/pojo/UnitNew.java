package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 单元表
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UnitNew extends Model<UnitNew> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long courseId;
    /**
     * 单元名
     */
    private String unitName;
    /**
     * 课程单元拼接名 - 用与导入词/句关联单元使用
     */
    private String jointName;
    /**
     * 删除状态: 1:未删除(默认)  2:已删除
     */
    @TableField("delStatus")
    private Integer delStatus;
    /**
     * 单元顺序，用于判断当前单元的下一单元是哪个
     */
    private Integer unitIndex;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
