package com.zhidejiaoyu.common.pojo;

import java.util.Date;
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
 * @since 2019-05-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LetterListen extends Model<LetterListen> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 单元id
     */
    private Integer unitId;
    /**
     * 字母id
     */
    private Integer letterId;
    /**
     * 状态1，正在学习 2，已经学习
     */
    private Integer state;
    /**
     * 黄金记忆点时间
     */
    private Date push;
    /**
     * 记忆强度
     */
    private Integer memoryStrength;
    /**
     * 学生id
     */
    private Integer studentId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
