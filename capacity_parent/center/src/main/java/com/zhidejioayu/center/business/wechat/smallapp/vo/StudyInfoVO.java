package com.zhidejioayu.center.business.wechat.smallapp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 学习记录数据（飞行记录）
 *
 * @author: wuchenxi
 * @date: 2020/2/17 16:33:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyInfoVO implements Serializable {

    /**
     * 学习时间
     */
    private String learnTime;

    /**
     * 在线时长
     */
    private String onlineTime;

    /**
     * 学习模块
     */
    private String studyModel;
}
