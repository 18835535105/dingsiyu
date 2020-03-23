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
 * 抽奖记录表
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DrawRecord extends Model<DrawRecord> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 学生id
     */
    private Integer studentId;
    /**
     * 抽奖时间
     */
    private Date createTime;
    /**
     * 抽取到的奖项名称
     */
    private String name;
    /**
     * 抽奖说明
     */
    @TableField("`explain`")
    private String explain;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
