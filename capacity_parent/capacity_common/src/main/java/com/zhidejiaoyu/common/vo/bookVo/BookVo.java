package com.zhidejiaoyu.common.vo.bookVo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 单词本和句子本列表展示内容
 *
 * @author wuchenxi
 * @date 2018年5月19日 下午3:59:47
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class BookVo implements Serializable {

    /**
     * 单词/例句id
     */
    private Long id;

    /**
     * 音标
     */
    private String soundMark;

    /**
     * 读音地址
     */
    private String readUrl;

    /**
     * 单词/例句英文
     */
    private String content;

    /**
     * 单词/例句中文
     */
    private String chinese;

    /**
     * 单词图片url
     */
    private String pictureUrl;

}
