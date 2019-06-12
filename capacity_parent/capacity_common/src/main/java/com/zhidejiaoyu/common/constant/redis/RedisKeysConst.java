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
    public static final String PREFIX = "simple_student";

    /**
     * 当前单元下所有单词信息
     */
    public static final String WORD_INFO__IN_UNIT = "word_info_in_unit:";

    /**
     * 当前学生 当前课程下 所有单元信息
     * 数据结构hash  key - WORD_INFO_IN_STUDENT_COURSE:学生id:课程id
     */
    public static final String WORD_INFO_IN_STUDENT_COURSE = "unit_in_student_course:";

    /**
     * 当前课程下每个单元的单词总数量
     * 数据结构hash key - WORD_COUNT_WITH_UNIT_IN_COURSE:课程id
     */
    public static final String WORD_COUNT_WITH_UNIT_IN_COURSE = "word_count_with_unit_in_course:";

    /**
     * 当前课程下所有的单词数
     * 数据结构hash key - WORD_COUNT_WITH_COURSE:课程id
     */
    public static final String WORD_COUNT_WITH_COURSE = "word_count_with_course:";

    /**
     * 当前课程下所有的单词数
     * 数据结构hash key - WORD_COUNT_WITH_COURSE:课程id
     */
    public static final String DRAW_COUNT_WITH_NAME = "draw_count_with_name:";

    /**
     * 当前单元下所有单词数
     * 数据结构hash key - WORD_COUNT_WITH_UNIT:单元id
     */
    public static final String WORD_COUNT_WITH_UNIT = "word_count_with_unit:";

    /**
     * 当前学生当前模块下所有课程信息
     * key - All_COURSE_WITH_STUDENT_IN_TYPE:学生id:模块名称
     */
    public static final String ALL_COURSE_WITH_STUDENT_IN_TYPE = "all_course_with_student_in_type:";

    /**
     * 所有等级信息
     */
    public static final String ALL_LEVEL= "all_level";

    /**
     * 统计在线人数
     */
    public static final String ONLINE_USER = "ONLINE_USER";

    /**
     * 用于存储学生时长信息及学生信息
     */
    public static final String SESSION_MAP = "SESSION_MAP";

    /**
     * 存储学生 id 与其 sessionId 对应关系
     */
    public static final String LOGIN_SESSION = "LOGIN_SESSION";

    /**
     * 奖励排行上升或者下降名次
     */
    public static final String SIMPLE_STUDENT_RANKING = "simple_student_ranking";

    /**
     * 当前单元下所有单词信息
     */
    public static final String WORD_INFO_IN_UNIT = "WORD_INFO_IN_UNIT:";

    public static final String SENTENCE_INFO_IN_UNIT = "SENTENCE_INFO_IN_UNIT:";

    /**
     * 学生保存测试记录时记录学生 id 及开始测试时间；学生再次保存测试记录时验证是不是重复
     * key: TEST_SUBMIT:studentId  value:学生测试开始时间
     */
    public static final String TEST_SUBMIT = "TEST_SUBMIT";

    /**
     * 用于存储已经保存过时长信息的登录时间，防止在线时长重复保存导致在线时长比实际大问题
     * key: SAVE_LOGIN_TIME:studentId value:保存的在线时长中的登录时间
     */
    public static final String SAVE_LOGIN_TIME = "SAVE_LOGIN_TIME";

    /**
     * 存储音节信息
     */
    public static final String PHONETIC_SYMBOL = "PHONETIC_SYMBOL";

    /**
     * 存储学生是否是第一次登录信息
     */
    public static final String FIRST_LOGIN = "FIRST_LOGIN";

    /**
     * 记录学生登出时间
     */
    public static final String STUDENT_LOGINOUT_TIME = "STUDENT_LOGINOUT_TIME";
}
