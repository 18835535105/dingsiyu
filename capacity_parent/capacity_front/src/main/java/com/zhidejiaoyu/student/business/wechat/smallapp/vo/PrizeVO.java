package com.zhidejiaoyu.student.business.wechat.smallapp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 奖品展示数据
 *
 * @author: wuchenxi
 * @date: 2020/2/18 17:36:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrizeVO implements Serializable {

    private String imgUrl;

    private Integer gold;

    private String name;
}
