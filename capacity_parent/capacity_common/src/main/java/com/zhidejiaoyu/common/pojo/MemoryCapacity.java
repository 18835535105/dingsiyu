package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zdjy
 * @since 2019-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MemoryCapacity extends Model<MemoryCapacity> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 挑战等级
     */
    private Integer grade;
    /**
     * 获得金币
     */
    private Integer gold;
    /**
     * 挑战时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 学生id
     */
    @TableField("student_id")
    private Long studentId;

    /**
     *类型
     */
    private Integer type;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
