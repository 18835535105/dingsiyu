package com.zhidejiaoyu.student.business.index.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 首页左上角头像数据展示
 *
 * @author: wuchenxi
 * @date: 2019/12/27 14:40:40
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClickPictureVO {

    private Integer sex;


    private Integer myGold;

    /**
     * 我的等级
     */
    private String childName;
    /**
     * 距离下一等级还差多少金币
     */
    private Integer jap;

    /**
     * 我的等级图片
     */
    private String imgUrl;

    /**
     * 下一级等级名
     */
    private String childNameBelow;

    /**
     * 下一级金币数量
     */
    private Integer japBelow;

    /**
     * 下一级等级图片
     */
    private String imgUrlBelow;

    /**
     * 今日获得金币数
     */
    private Double myThisGold;
}
