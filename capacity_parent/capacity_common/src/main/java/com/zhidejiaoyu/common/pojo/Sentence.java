package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

public class Sentence extends Model<Sentence> {
	/** 例句主建id */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 例句英文 */
    private String centreExample;
    /** 例句翻译 */
    private String centreTranslate;
    /** 例句英文干扰 */
    private String ExampleDisturb;
    /** 例句中文干扰 */
    private String TranslateDisturb;

	@Override
	public String toString() {
		return "Sentence [id=" + id + ", centreExample=" + centreExample + ", centreTranslate=" + centreTranslate
				+ ", ExampleDisturb=" + ExampleDisturb + ", TranslateDisturb=" + TranslateDisturb + "]";
	}

	public Long getId() {
		return id;
	}

	public String getExampleDisturb() {
		return ExampleDisturb;
	}

	public void setExampleDisturb(String exampleDisturb) {
		ExampleDisturb = exampleDisturb;
	}

	public String getTranslateDisturb() {
		return TranslateDisturb;
	}

	public void setTranslateDisturb(String translateDisturb) {
		TranslateDisturb = translateDisturb;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String CentreExample() {
		return centreExample;
	}

	public void setCentreExample(String centreExample) {
		this.centreExample = centreExample;
	}

	public String getCentreExample() {
		return centreExample;
	}

	public String getCentreTranslate() {
		return centreTranslate;
	}

	public void setCentreTranslate(String centreTranslate) {
		this.centreTranslate = centreTranslate;
	}

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}