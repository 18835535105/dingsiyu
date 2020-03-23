package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2018-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Consume extends Model<Consume> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 消耗名称
     */
    private String name;
    /**
     * 1,金币  2,钻石
     */
    private Integer type;
    /**
     * 消耗数量
     */
    private Integer number;
    /**
     * 学生id
     */
    private Integer studentId;
    /**
     * 1,为正价   2为减少
     */
    private Integer state;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
