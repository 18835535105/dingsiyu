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
 * @since 2019-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Letter extends Model<Letter> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 大写字母
     */
    private String bigLetter;
    /**
     * 小写字母
     */
    private String lowercaseLetters;
    /**
     * 单元id
     */
    private Integer unitId;
    /**
     * 类型
     */
    private String type;
    /**
     * gif图片
     */
    private String gifUrl;

    /**
     * 录音地址
     */
    private String audioUrl;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
