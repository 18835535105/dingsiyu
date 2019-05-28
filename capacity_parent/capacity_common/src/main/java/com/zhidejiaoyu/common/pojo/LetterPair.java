package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
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
 * 字母配对记忆表
 * </p>
 *
 * @author zdjy
 * @since 2019-05-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LetterPair extends Model<LetterPair> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 字母id
     */
    private Integer letterId;
    /**
     * 学生id
     */
    private Integer studentId;
    /**
     * 黄金记忆时间
     */
    private Date push;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 记忆强度
     */
    private Integer memoryStrength;

    private Integer unitId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
