package com.zhidejiaoyu.student.business.index.vo;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 首页右侧小人显示需要复习的单词和句型数
 *
 * @author: wuchenxi
 * @date: 2019/12/21 14:51:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NeedReviewCountVO implements Serializable {

    private String partUrl;

    private Integer vocabularyCount;

    private Integer sentenceCount;

}
