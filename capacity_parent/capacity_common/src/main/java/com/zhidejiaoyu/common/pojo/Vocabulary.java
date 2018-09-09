package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;

/**
 * 词汇
 * 
 * @author Administrator
 */
public class Vocabulary implements Serializable {

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

	public Integer getDelStatus() {
		return delStatus;
	}

	public void setDelStatus(Integer delStatus) {
		this.delStatus = delStatus;
	}

	public Double getMemory_strength() {
		return memory_strength;
	}

	public void setMemory_strength(Double memory_strength) {
		this.memory_strength = memory_strength;
	}

	public String getSyllable() {
		return syllable;
	}

	public void setSyllable(String syllable) {
		this.syllable = syllable;
	}

	public String getStrClassify() {
		return strClassify;
	}

	public void setStrClassify(String strClassify) {
		this.strClassify = strClassify;
	}

	@Override
	public String toString() {
		return "Vocabulary [id=" + id + ", word=" + word + ", wordChinese=" + wordChinese + ", exampleEnglish="
				+ exampleEnglish + ", exampleChinese=" + exampleChinese + ", centreExample=" + centreExample
				+ ", centreTranslate=" + centreTranslate + ", tallExample=" + tallExample + ", tallTranslate="
				+ tallTranslate + ", explain_=" + explain_ + ", status=" + status + ", upage=" + upage + ", match="
				+ match + ", think=" + think + ", record=" + record + ", recordpicname=" + recordpicname
				+ ", recordpicurl=" + recordpicurl + ", discriminate=" + discriminate + ", stat=" + stat
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + ", courseUnit=" + courseUnit
				+ ", course_id=" + course_id + ", joint_name=" + joint_name + ", strClassify=" + strClassify + "]";
	}

	public String getCentreExample() {
		return centreExample;
	}

	public void setCentreExample(String centreExample) {
		this.centreExample = centreExample;
	}

	public String getCentreTranslate() {
		return centreTranslate;
	}

	public void setCentreTranslate(String centreTranslate) {
		this.centreTranslate = centreTranslate;
	}

	public String getTallExample() {
		return tallExample;
	}

	public void setTallExample(String tallExample) {
		this.tallExample = tallExample;
	}

	public String getTallTranslate() {
		return tallTranslate;
	}

	public void setTallTranslate(String tallTranslate) {
		this.tallTranslate = tallTranslate;
	}

	public String getJoint_name() {
		return joint_name;
	}

	public void setJoint_name(String joint_name) {
		this.joint_name = joint_name;
	}

	public String getCourse_id() {
		return course_id;
	}

	public void setCourse_id(String course_id) {
		this.course_id = course_id;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getUpage() {
		return upage;
	}

	public void setUpage(String upage) {
		this.upage = upage;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public String getThink() {
		return think;
	}

	public void setThink(String think) {
		this.think = think;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public String getRecordpicname() {
		return recordpicname;
	}

	public void setRecordpicname(String recordpicname) {
		this.recordpicname = recordpicname;
	}

	public String getRecordpicurl() {
		return recordpicurl;
	}

	public void setRecordpicurl(String recordpicurl) {
		this.recordpicurl = recordpicurl;
	}

	public String getDiscriminate() {
		return discriminate;
	}

	public void setDiscriminate(String discriminate) {
		this.discriminate = discriminate;
	}

	public void setExplain_(String explain_) {
		this.explain_ = explain_;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word == null ? null : word.trim();
	}

	public String getWordChinese() {
		return wordChinese;
	}

	public void setWordChinese(String wordChinese) {
		this.wordChinese = wordChinese == null ? null : wordChinese.trim();
	}

	public String getExampleEnglish() {
		return exampleEnglish;
	}

	public void setExampleEnglish(String exampleEnglish) {
		this.exampleEnglish = exampleEnglish == null ? null : exampleEnglish.trim();
	}

	public String getExampleChinese() {
		return exampleChinese;
	}

	public void setExampleChinese(String exampleChinese) {
		this.exampleChinese = exampleChinese == null ? null : exampleChinese.trim();
	}

	public String getExplain_() {
		return explain_;
	}

	public void setExplain(String explain_) {
		this.explain_ = explain_ == null ? null : explain_.trim();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getCourseUnit() {
		return courseUnit;
	}

	public void setCourseUnit(String courseUnit) {
		this.courseUnit = courseUnit == null ? null : courseUnit.trim();
	}
}