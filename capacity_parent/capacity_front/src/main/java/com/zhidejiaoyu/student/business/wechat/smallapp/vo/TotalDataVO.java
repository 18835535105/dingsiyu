package com.zhidejiaoyu.student.business.wechat.smallapp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 公用数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 17:22:22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalDataVO implements Serializable {

    private String headImg;

    private String studentName;

    /**
     * 问候语
     */
    private String say;

    /**
     * 剩余金币数
     */
    private String systemGold;
}
