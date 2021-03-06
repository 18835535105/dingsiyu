package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2019-05-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LetterWrite extends Model<LetterWrite> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 学生id
     */
    private Integer studentId;
    /**
     * 单元id
     */
    private Integer unitId;
    /**
     * 字母id
     */
    private Integer letterId;
    /**
     * 黄金记忆时间
     */
    private Date push;
    /**
     * 记忆强度
     */
    private Double memoryStrength;
    /**
     * 1,正在学习 2，已经学习
     */
    private Integer state;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
