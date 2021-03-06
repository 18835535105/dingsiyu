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
    private Double hitRate;
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

    /**
     *飞船名 不参加书库操作
     * @return
     */
    @TableField(exist = false)
    private String name;
    /**
     * 飞船等级 不参加书库操作
     * @return
     */
    @TableField(exist = false)
    private String grade;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
