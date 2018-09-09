package com.zhidejiaoyu.student.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 单词/例句播放机
 *
 * @author wuchenxi
 * @date 2018/5/22 11:14
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)public class PlayerVo {

    /**
     * 播放机总播放量
     */
    private Integer total;

    /**
     * 播放机具体内容
     */
    private List<BookVo> playerList;

    /**
     * 单词卡片语句播放机内容
     */
    private List<MemoryStudyVo> voiceList;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<BookVo> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<BookVo> playerList) {
        this.playerList = playerList;
    }

    public List<MemoryStudyVo> getVoiceList() {
        return voiceList;
    }

    public void setVoiceList(List<MemoryStudyVo> voiceList) {
        this.voiceList = voiceList;
    }
}
