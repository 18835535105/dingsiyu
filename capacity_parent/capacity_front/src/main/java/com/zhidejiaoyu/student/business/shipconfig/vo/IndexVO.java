package com.zhidejiaoyu.student.business.shipconfig.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 飞船配置首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/27 16:42:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexVO implements Serializable {

    /**
     * 源分战力值
     */
    private Integer sourcePoser;

    /**
     * 武器图片路径
     */
    private String weaponsUrl;

    /**
     * 装甲图片路径
     */
    private String armorUrl;

    /**
     * 飞船图片路径
     */
    private String shipUrl;

    /**
     * 导弹图片路径
     */
    private String missileUrl;

    /**
     * 资源图片路径
     */
    private String sourceUrl;

    /**
     * 勋章图片地址
     */
    private List<String> medalUrl;

    /**
     * 背景图片地址
     */
    private String skinImgUrl;
}
