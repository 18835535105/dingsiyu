package com.zhidejiaoyu.common.vo.simple.studentInfoVo;

import com.github.pagehelper.PageInfo;
import lombok.Data;

/**
 * 学生查看等级信息
 *
 * @author wuchenxi
 * @date 2018/10/8
 */
@Data
public class LevelVo {

    private String headUrl;

    private String nickname;

    /**
     * 当前等级图片地址
     */
    private String levelImgUrl;

    /**
     * 当前等级的父等级索引，用于判断等级中哪些黑点被点亮
     */
    private Integer parentLevelIndex;

    /**
     * 当前子等级在父等级中的索引，用于计算等级进度条
     */
    private Integer childLevelIndex;

    /**
     * 当前等级名称
     */
    private String childName;

    /**
     * 勋章路径集合
     */
    private PageInfo<String> medalImgUrl;

    /**
     * 是否显示膜拜拳头
     */
    private Boolean showFist;
}
