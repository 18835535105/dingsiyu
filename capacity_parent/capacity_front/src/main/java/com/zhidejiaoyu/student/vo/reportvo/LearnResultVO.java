package com.zhidejiaoyu.student.vo.reportvo;

/**
 * 学习成果VO
 *
 * @author wuchenxi
 * @date 2018/7/19
 */
public class LearnResultVO {

    /**
     * 掌握的类型
     * <ul>
     * <li>智慧单词</li>
     * <li>抢分举行</li>
     * <li>原汁原文</li>
     * <li>口语跟读</li>
     * </ul>
     */
    private String type;

    /**
     * 掌握的个数
     */
    private Integer master;

    /**
     * 应掌握的个数
     */
    private Integer shouldMaster;

    /**
     * 掌握率
     */
    private Integer masterRate;

    public Integer getMaster() {
        return master;
    }

    public void setMaster(Integer master) {
        this.master = master;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getShouldMaster() {
        return shouldMaster;
    }

    public void setShouldMaster(Integer shouldMaster) {
        this.shouldMaster = shouldMaster;
    }

    public Integer getMasterRate() {
        return masterRate;
    }

    public void setMasterRate(Integer masterRate) {
        this.masterRate = masterRate;
    }

    @Override
    public String toString() {
        return "LearnResultVO{" +
                "type='" + type + '\'' +
                ", master=" + master +
                ", shouldMaster=" + shouldMaster +
                ", masterRate=" + masterRate +
                '}';
    }
}
