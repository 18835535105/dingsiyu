package com.zhidejiaoyu.common.vo.wechat.qy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 李糖心Q&A问题及可能的答案
 *
 * @author wuchenxi
 * @date 2020-07-21 14:03:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QaVO implements Serializable {

    /**
     * 答案
     */
    private String answer;

    /**
     * 问题
     */
    private String question;

    /**
     * 答案读音
     */
    private String url;

    /**
     * 问题id
     */
    private Integer id;

    /**
     * 问题序号
     */
    private String num;
}
