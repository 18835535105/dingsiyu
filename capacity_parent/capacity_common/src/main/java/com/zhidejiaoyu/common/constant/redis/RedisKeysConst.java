package com.zhidejiaoyu.common.constant.redis;

/**
 * redis key 命名常量类
 *
 * @author wuchenxi
 * @date 2018/11/12
 */
public interface RedisKeysConst {

    /**
     * 清学版redis缓存数据 key
     */
    String PREFIX = "simple_student";

    /**
     * 摸底测试
     */
    String TEST_BEFORE_STUDY = "TEST_BEFORE_STUDY:";
    /**
     * 学生已经学习过的模块
     */
    String LOOK_GUIDE = "LOOK_GUIDE:";
    /**
     * 当前课程下每个单元的单词总数量
     * 数据结构hash key - WORD_COUNT_WITH_UNIT_IN_COURSE:课程id
     */
    String WORD_COUNT_WITH_UNIT_IN_COURSE = "word_count_with_unit_in_course:";

    /**
     * 当前课程下所有的单词数
     * 数据结构hash key - WORD_COUNT_WITH_COURSE:课程id
     */
    String WORD_COUNT_WITH_COURSE = "word_count_with_course:";

    /**
     * 当前课程下所有的单词数
     * 数据结构hash key - WORD_COUNT_WITH_COURSE:课程id
     */
    String DRAW_COUNT_WITH_NAME = "draw_count_with_name:";

    /**
     * 当前单元下所有单词数
     * 数据结构hash key - WORD_COUNT_WITH_UNIT:单元id
     */
    String WORD_COUNT_WITH_UNIT = "word_count_with_unit:";

    /**
     * 当前学生当前模块下所有课程信息
     * key - All_COURSE_WITH_STUDENT_IN_TYPE:学生id:模块名称
     */
    String ALL_COURSE_WITH_STUDENT_IN_TYPE = "all_course_with_student_in_type:";

    /**
     * 所有等级信息
     */
    String ALL_LEVEL = "all_level";

    /**
     * 统计在线人数
     */
    String ZSET_ONLINE_USER = "ZSET_ONLINE_USER";

    /**
     * 用于存储学生时长信息及学生信息
     */
    String SESSION_MAP = "SESSION_MAP";

    /**
     * 存储学生 id 与其 sessionId 对应关系
     */
    String LOGIN_SESSION = "LOGIN_SESSION";

    /**
     * 当前单元下所有单词信息
     */
    String WORD_INFO_IN_UNIT = "WORD_INFO_IN_UNIT:";

    String SENTENCE_INFO_IN_UNIT = "SENTENCE_INFO_IN_UNIT:";

    /**
     * 学生保存测试记录时记录学生 id 及开始测试时间；学生再次保存测试记录时验证是不是重复
     * key: TEST_SUBMIT:studentId  value:学生测试开始时间
     */
    String TEST_SUBMIT = "TEST_SUBMIT";

    /**
     * 存储音节信息
     */
    String PHONETIC_SYMBOL = "PHONETIC_SYMBOL";

    /**
     * 存储学生是否是第一次登录信息
     */
    String FIRST_LOGIN = "FIRST_LOGIN";

    /**
     * 被标记为异地登录被挤掉的 sessionId
     */
    String MULTIPLE_LOGIN_SESSION_ID = "MULTIPLE_LOGIN_SESSION_ID:";

    /**
     * 学生是否已充值标识，有记录的学生id说明已经充值过
     */
    String IS_PAID = "IS_PAID";

    /**
     * 用于标识学生指定的group中的单词首次答错是否需要额外增加50%的记忆强度
     */
    String FIRST_FALSE_ADD = "FIRST_FALSE_ADD";
}
