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
 * 字母，音节，字母宝典关联的单元表

 * </p>
 *
 * @author zdjy
 * @since 2019-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LetterUnit extends Model<LetterUnit> {

    private static final long serialVersionUID = 1L;

    /**
     * 单元 id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 单元名称
     */
    private String unitName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
