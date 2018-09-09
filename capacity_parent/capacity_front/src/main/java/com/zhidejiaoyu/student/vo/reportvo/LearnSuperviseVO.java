package com.zhidejiaoyu.student.vo.reportvo;

/**
 * 学习监督VO
 * @author wuchenxi
 * @date 2018/7/20
 */
public class LearnSuperviseVO {

    /**
     * 已学单词总个数
     */
    private Integer wordCount;

    /**
     * 已学例句总个数
     */
    private Integer sentenceCount;

    /**
     * 已学课文总个数
     */
    private Integer textCount;

    /**
     * 已学口语总个数
     */
    private Integer spokenCount;
    /**
     * 已学知识点总数量
     */
    private Integer learnedCount;

    /**
     * 战胜学生的百分比
     */
    private Integer defeatRate;

    /**
     * 总在线时长（小时)
     */
    private Integer totalOnlineTime;

    /**
     * 总有效时长(小时)
     */
    private Integer totalValidTime;

    /**
     * 学习效率
     */
    private Integer efficiency;

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public Integer getSentenceCount() {
        return sentenceCount;
    }

    public void setSentenceCount(Integer sentenceCount) {
        this.sentenceCount = sentenceCount;
    }

    public Integer getTextCount() {
        return textCount;
    }

    public void setTextCount(Integer textCount) {
        this.textCount = textCount;
    }

    public Integer getSpokenCount() {
        return spokenCount;
    }

    public void setSpokenCount(Integer spokenCount) {
        this.spokenCount = spokenCount;
    }

    public Integer getLearnedCount() {
        return learnedCount;
    }

    public void setLearnedCount(Integer learnedCount) {
        this.learnedCount = learnedCount;
    }

    public Integer getDefeatRate() {
        return defeatRate;
    }

    public void setDefeatRate(Integer defeatRate) {
        this.defeatRate = defeatRate;
    }

    public Integer getTotalOnlineTime() {
        return totalOnlineTime;
    }

    public void setTotalOnlineTime(Integer totalOnlineTime) {
        this.totalOnlineTime = totalOnlineTime;
    }

    public Integer getTotalValidTime() {
        return totalValidTime;
    }

    public void setTotalValidTime(Integer totalValidTime) {
        this.totalValidTime = totalValidTime;
    }

    public Integer getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(Integer efficiency) {
        this.efficiency = efficiency;
    }

    @Override
    public String toString() {
        return "LearnSuperviseVO{" +
                "wordCount=" + wordCount +
                ", sentenceCount=" + sentenceCount +
                ", textCount=" + textCount +
                ", spokenCount=" + spokenCount +
                ", learnedCount=" + learnedCount +
                ", defeatRate=" + defeatRate +
                ", totalOnlineTime=" + totalOnlineTime +
                ", totalValidTime=" + totalValidTime +
                ", efficiency=" + efficiency +
                '}';
    }
}
