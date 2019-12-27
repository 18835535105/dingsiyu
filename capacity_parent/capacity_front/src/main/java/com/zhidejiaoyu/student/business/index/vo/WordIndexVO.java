package com.zhidejiaoyu.student.business.index.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 单词首页接口响应数据
 *
 * @author: wuchenxi
 * @date: 2019/12/27 09:40:40
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordIndexVO implements Serializable {

    private Long studentId;

    private Integer role;

    private String account;

    private String StudentName;

    /**
     * 头像
     */
    private String headUrl;

    /**
     * 宠物头像
     */
    private String partUrl;

    /**
     * 宠物名
     */
    private String petName;

    private String schoolName;

    /**
     * 学习效率
     */
    private String efficiency;

    /**
     * 在线时长
     */
    private Integer online;

    /**
     * 有效时长
     */
    private Integer valid;

}
