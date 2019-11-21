package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
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
 * 例句表
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SentenceNew extends Model<SentenceNew> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 例句英文
     */
    @TableField("centreExample")
    private String centreExample;
    /**
     * 例句翻译
     */
    @TableField("centreTranslate")
    private String centreTranslate;
    /**
     * 例句英文干扰
     */
    @TableField("ExampleDisturb")
    private String ExampleDisturb;
    /**
     * 例句中文干扰
     */
    @TableField("TranslateDisturb")
    private String TranslateDisturb;
    /**
     * 句型本地读音地址
     */
    private String readUrl;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
