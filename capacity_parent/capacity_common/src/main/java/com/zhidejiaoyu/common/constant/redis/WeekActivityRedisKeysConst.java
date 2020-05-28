package com.zhidejiaoyu.common.constant.redis;

/**
 * 每周活动缓存key常量
 *
 * @author: wuchenxi
 * @date: 2020/5/27 10:12:12
 */
public interface WeekActivityRedisKeysConst {


    /**
     * 每周活动校区排行常量
     * 数据类型：zset
     * key:WEEK_ACTIVITY_SCHOOL_RANK:schoolAdminId
     * field:studentId
     * score:当前挑战进度
     */
    String WEEK_ACTIVITY_SCHOOL_RANK = "WEEK_ACTIVITY_SCHOOL_RANK:";

    /**
     * 每周活动各个学生完成情况
     * 数据类型：hash
     * key:WEEK_ACTIVITY_LIST
     * field:studentId
     * value:com.zhidejiaoyu.student.business.activity.vo.AwardListVO.ActivityList
     */
    String WEEK_ACTIVITY_LIST = "WEEK_ACTIVITY_LIST";
}
