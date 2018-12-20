package com.zhidejiaoyu.common.constant.redis;

/**
 * redis key 命名常量类
 *
 * @author wuchenxi
 * @date 2018/11/12
 */
public class RedisKeysConst {

    /**
     * 清学版redis缓存数据 key
     */
    public static final String PREFIX = "capacity_student";

    /**
     * 当前单元下所有单词信息
     */
    public static final String WORD_INFO_IN_UNIT = "WORD_INFO_IN_UNIT:";

    public static final String SENTENCE_INFO_IN_UNIT = "SENTENCE_INFO_IN_UNIT:";

    /**
     * 当前学生 当前课程下 所有单元信息
     * 数据结构hash  key - WORD_INFO_IN_STUDENT_COURSE:学生id:课程id
     */
    public static final String WORD_INFO_IN_STUDENT_COURSE = "WORD_INFO_IN_STUDENT_COURSE:";

    /**
     * 当前课程下每个单元的单词总数量
     * 数据结构hash key - WORD_COUNT_WITH_UNIT_IN_COURSE:课程id
     */
    public static final String WORD_COUNT_WITH_UNIT_IN_COURSE = "WORD_COUNT_WITH_UNIT_IN_COURSE:";

    /**
     * 当前课程下所有的单词数
     * 数据结构hash key - WORD_COUNT_WITH_COURSE:课程id
     */
    public static final String WORD_COUNT_WITH_COURSE = "WORD_COUNT_WITH_COURSE:";

    /**
     * 当前单元下所有单词数
     * 数据结构hash key - WORD_COUNT_WITH_UNIT:单元id
     */
    public static final String WORD_COUNT_WITH_UNIT = "WORD_COUNT_WITH_UNIT:";

    /**
     * 当前学生当前模块下所有课程信息
     * key - All_COURSE_WITH_STUDENT_IN_TYPE:学生id:模块名称
     */
    public static final String All_COURSE_WITH_STUDENT_IN_TYPE = "All_COURSE_WITH_STUDENT_IN_TYPE:";

    /**
     * 所有等级信息
     */
    public static final String ALL_LEVEL= "ALL_LEVEL";
}
