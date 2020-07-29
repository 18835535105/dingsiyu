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
 * @since 2019-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LetterVocabulary extends Model<LetterVocabulary> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 单元
     */
    private Integer unitId;
    /**
     * 字母
     */
    private String letter;
    /**
     * 所属大类
     */
    private String majorCategories;
    /**
     * 所属小类
     */
    private String subordinateCategory;
    /**
     * 单词
     */
    private String vocabulary;
    /**
     * 音标
     */
    private String phoneticSymbol;
    /**
     * 音节
     */
    private String syllable;

    /**
     * 单词读音地址
     */
    @TableField("mp3_url")
    private String mp3Url;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
