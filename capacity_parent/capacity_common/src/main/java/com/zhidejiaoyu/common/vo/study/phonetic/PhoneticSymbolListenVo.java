package com.zhidejiaoyu.common.vo.study.phonetic;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 音节辨音
 *
 * @author wuchenxi
 * @date 2019-05-20
 */
@Data
public class PhoneticSymbolListenVo implements Serializable {

    /**
     * 音节 id
     */
    private Integer id;

    /**
     * 音节信息
     */
    private String phonetic;

    /**
     * 学习进度
     */
    private Integer plan;

    /**
     * 总进度
     */
    private Integer total;

    /**
     * 音标读音
     */
    private String audioUrl;

    /**
     * 答案集合
     */
    private List<Topic> topics;
}
