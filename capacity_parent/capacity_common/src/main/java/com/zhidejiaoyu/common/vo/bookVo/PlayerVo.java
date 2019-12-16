package com.zhidejiaoyu.common.vo.bookVo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhidejiaoyu.common.vo.study.MemoryStudyVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 单词/例句播放机
 *
 * @author wuchenxi
 * @date 2018/5/22 11:14
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class PlayerVo implements Serializable {

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
}
