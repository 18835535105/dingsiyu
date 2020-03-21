package com.zhidejiaoyu.student.business.shipconfig.vo;

import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * pk、副本试题数据
 *
 * @author: wuchenxi
 * @date: 2020/3/19 10:38:38
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PkInfoVO implements Serializable {

    /**
     * 被挑战者
     */
    private Challenged challenged;

    /**
     * 挑战者
     */
    private Challenged originator;

    /**
     * 试题
     */
    private List<SubjectsVO> subject;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Challenged {
        /**
         * 飞船图片
         */
        private String imgUrl;

        /**
         * 战斗数据
         */
        private IndexVO.BaseValue battle;

        /**
         * 飞船型号
         */
        private String grade;

        /**
         * 飞船名称
         */
        private String name;

    }
}
