package com.zhidejiaoyu.common.Vo.student;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 学习路径页面数据vo
 *
 * @author wuchenxi
 * @date 2018/8/27
 */
@Data
public class StudyLocusVo {
    /**
     * 当前单元获取的证书
     */
    private PageInfo<CcieVo> cciePageInfo;

    /**
     * 当前单元获取的勋章
     */
    private PageInfo<String> medalUrlPageInfo;

    /**
     * 战胜率 10%
     */
    private String victoryRate;

    /**
     * 当前单元获取的金币数
     */
    private Integer goldCount;

    /**
     * 当前课程下的所有单元
     */
    private PageInfo<Map<String, Object>> unitPageInfo;

    /**
     * 所有课程
     */
    private List<Map<String, Object>> courseList;

    /**
     * 通关等级图片url
     */
    private List<String> levelList;
}
