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
    private Double memoryStrength;

    private Integer unitId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
