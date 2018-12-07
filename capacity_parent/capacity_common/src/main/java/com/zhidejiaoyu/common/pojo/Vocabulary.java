package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 词汇
 * 
 * @author Administrator
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Vocabulary extends Model<Vocabulary> {
	@TableId(type = IdType.AUTO)
	private Long id;

	/** 单词 */
	private String word;

	/** 公共单词中文意思 */
	private String wordChinese;

	/** 例句英文 */
	private String exampleEnglish;

	/** 例句中文 */
	private String exampleChinese;

	/** 初中例句英文 */
	private String centreExample;

	/** 初中例句中文 */
	private String centreTranslate;

	/** 高中例句英文 */
	private String tallExample;

	/** 高中例句中文 */
	private String tallTranslate;

	/** 拓展 */
	private String explain_;

	/** 单词状态 1 = 开启（默认）， 2 = 关闭 */
	private Integer status;

	/** 用法 */
	private String upage;

	/** 搭配 */
	private String match;

	/** 联想 */
	private String think;

	/** 助记 */
	private String record;

	/** 助记图片名 */
	private String recordpicname;

	/** 助记图片url地址 */
	private String recordpicurl;

	/** 辨析 */
	private String discriminate;

	/** 开启状态中文 */
	private String stat;

	/** 创建时间 */
	private String createTime;

	private String updateTime;

	private String courseUnit;
	private String course_id;
	private String joint_name;
	private String strClassify;
	private Double memory_strength;
	
	/** 音节 */
	private String syllable;

	/** 删除 1 = 开启（默认）， 2 = 关闭*/
	private Integer delStatus;

	/**
	 * 音标
	 */
	@TableField("sound_mark")
	private String soundMark;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}