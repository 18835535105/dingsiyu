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
 * @since 2019-09-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SchoolHours extends Model<SchoolHours> {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 校管id
     */
    @TableField(value = "admin_id")
    private Integer adminId;
    /**
     * 类型格式：充课类型:数量,
     */
    private String type;
    /**
     * 队长币
     */
    @TableField(value = "captain_coin")
    private String captainCoin;


    @Override
    protected Serializable pkVal() {
        return this.adminId;
    }

}
