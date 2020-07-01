package com.zhidejiaoyu.common.vo.currentdayofstudy;

import lombok.Data;

import java.io.Serializable;

/**
 * 飞行时长及飞行里程
 *
 * @author: wuchenxi
 * @date: 2020/7/1 16:30:30
 */
@Data
public class StudyTimeAndMileageVO implements Serializable {

    /**
     * 飞行时长（秒）
     */
    private Long time;

    /**
     * 飞行里程
     */
    private Integer mileage;
}
