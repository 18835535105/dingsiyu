package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zdjy
 * @since 2020-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Equipment extends Model<Equipment> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 飞船名称
     */
    private String name;
    /**
     * 1,飞船；2，武器；3，导弹，4，装甲
     */
    private Integer type;
    /**
     * 开启等级1-12
     */
    private Integer level;

    /**
     * 开启所需经验值
     *
     */
    private Long empiricalValue;
    /**
     * 飞船级别
     *
     */
    private String grade;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
