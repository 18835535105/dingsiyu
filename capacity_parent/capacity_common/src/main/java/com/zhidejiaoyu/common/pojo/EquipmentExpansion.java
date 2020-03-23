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
public class EquipmentExpansion extends Model<EquipmentExpansion> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 装备id
     */
    private Long equipmentId;
    /**
     * 耐久度
     */
    private Integer durability;
    /**
     * 普通攻击
     */
    private Integer commonAttack;
    /**
     * 源力攻击
     */
    private Integer sourceForceAttack;
    /**
     * 源力
     */
    private Integer sourceForce;
    /**
     * 命中率
     */
    private Integer hitRate;
    /**
     * 机动力
     */
    private Integer mobility;
    /**
     * 强化成度
     */
    private Integer intensificationDegree;
    /**
     * 图片地址
     */
    private String imgUrl;
    /**
     * 左侧图片地址
     */
    private String leftImgUrl;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
