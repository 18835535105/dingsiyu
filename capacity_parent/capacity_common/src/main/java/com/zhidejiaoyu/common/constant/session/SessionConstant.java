package com.zhidejiaoyu.common.constant.session;

/**
 * session常量
 *
 * @author: wuchenxi
 * @date: 2020/2/4 16:37:37
 */
public interface SessionConstant {

    /**
     * 如果该值为true，说明学生学习时首次答错，在原记忆强度基础上再 +50%
     */
    String FIRST_FALSE_ADD = "FIRST_FALSE_ADD";

    /**
     * 一键排课的group
     */
    String ONE_KEY_GROUP = "ONE_KEY_GROUP";

    /**
     * 一键排课group
     */
    String FREE_GROUP = "FREE_GROUP";

    /**
     * 自由学习和一键学习标识
     * <ul>
     * <li>1:一键学习</li>
     * <li>2:自由学习</li>
     * </ul>
     */
    String STUDY_FLAG = "STUDY_FLAG";

    /**
     * 学生正在学习的单元group序号
     */
    String STUDY_GROUP = "STUDY_GROUP";

}
