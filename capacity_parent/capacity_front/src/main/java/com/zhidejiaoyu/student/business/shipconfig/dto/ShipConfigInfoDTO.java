package com.zhidejiaoyu.student.business.shipconfig.dto;

import com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.IndexVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配置页面各个图片内容
 *
 * @author: wuchenxi
 * @date: 2020/4/2 09:48:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipConfigInfoDTO {

    private IndexVO.BaseValue baseValue;

    /**
     * 名称
     */
    private String name;

    /**
     * 强化度
     */
    private Integer degree;
}
