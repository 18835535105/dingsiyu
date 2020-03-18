package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
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
 * @since 2020-03-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PkCopyBase extends Model<PkCopyBase> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 飞船名称
     */
    private String name;
    /**
     * 飞船级别
     */
    private String levelName;
    /**
     * 1：单人副本（每天可PK）；2：校区副本（周六、周日可PK）
     */
    private Integer type;
    /**
     * 挑战周期。1：每天一次；2：每天2次；3：每天3次
     */
    private Integer challengeCycle;
    /**
     * 挑战人数。1：校区人数小于30人；2：校区人数大于30人
     */
    private Integer personNum;
    /**
     * 耐久度
     */
    private Integer durability;
    /**
     * 普通攻击
     */
    private Integer commonAttack;
    /**
     * 源分攻击
     */
    private Integer sourceForceAttack;
    /**
     * 源分次数
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
     * 图片路径
     */
    private String imgUrl;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
