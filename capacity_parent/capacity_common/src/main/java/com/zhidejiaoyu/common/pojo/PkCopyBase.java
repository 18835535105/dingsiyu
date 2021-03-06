package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
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

    /**
     * 奖励金币数
     * <ul>
     *     <li>单人副本挑战成功后才奖励</li>
     *     <li>校区副本只要参与挑战就有奖励</li>
     * </ul>
     */
    private Integer gold;

    /**
     * 每周最多奖励次数
     * <ul>
     *     <li>校区副本每周只有周六周日奖励</li>
     * </ul>
     */
    private Integer maxAwardCount;

    /**
     * 挑战成功后放入校区金币工厂的金币数
     * 只有校区副本挑战成功才有奖励j
     */
    private Integer schoolGold;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
