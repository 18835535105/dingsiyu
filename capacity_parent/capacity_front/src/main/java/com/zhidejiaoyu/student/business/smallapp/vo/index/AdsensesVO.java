package com.zhidejiaoyu.student.business.smallapp.vo.index;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 首页广告位数据
 *
 * @author: wuchenxi
 * @date: 2020/2/17 11:01:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsensesVO implements Serializable {

    /**
     * 图片链接
     */
    private String imgUrl;

    /**
     * 小程序或者公众号跳转名称
     */
    private String toUrl;

    /**
     * 1：公众号；2：小程序
     */
    private Integer type;

}
