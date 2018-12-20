package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("all")
public class Sentence extends Model<Sentence> {
	/** 例句主建id */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 例句英文 */
    @TableField("centreExample")
    private String centreExample;

    /** 例句翻译 */
	@TableField("centreTranslate")
    private String centreTranslate;

    /** 例句英文干扰 */
	@TableField("ExampleDisturb")
    private String ExampleDisturb;

    /** 例句中文干扰 */
	@TableField("TranslateDisturb")
    private String TranslateDisturb;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}