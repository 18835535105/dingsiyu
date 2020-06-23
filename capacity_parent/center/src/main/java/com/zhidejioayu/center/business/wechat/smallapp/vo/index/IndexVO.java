package com.zhidejioayu.center.business.wechat.smallapp.vo.index;

import com.zhidejiaoyu.student.business.wechat.smallapp.vo.TotalDataVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 首页数据展示
 *
 * @author: wuchenxi
 * @date: 2020/2/17 11:05:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexVO implements Serializable {

    /**
     * 广告位
     */
    private List<AdsensesVO> adsenses;

    /**
     * 头部公用数据
     */
    private TotalDataVO totalData;

}
