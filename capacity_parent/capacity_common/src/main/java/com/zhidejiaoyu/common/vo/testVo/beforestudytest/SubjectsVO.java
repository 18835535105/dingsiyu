package com.zhidejiaoyu.common.vo.testVo.beforestudytest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 学前测试响应结果
 *
 * @author: wuchenxi
 * @date: 2019/12/19 10:21:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectsVO implements Serializable {

    private Long unitId;

    private String readUrl;

    private String wordChinese;

    private String word;
}
