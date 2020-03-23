package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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

	private String readUrl;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}