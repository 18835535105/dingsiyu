package com.zhidejiaoyu.student.common.redis;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.mapper.simple.SimpleCourseMapper;
import com.zhidejiaoyu.common.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 业务逻辑中操作 redis 的常用操作逻辑
 *
 * @author wuchenxi
 * @date 2018/11/14
 */
@Slf4j
@Component
public class RedisOpt {

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private LevelMapper levelMapper;

    @Autowired
    private SentenceMapper sentenceMapper;

    @Autowired
    private PhoneticSymbolMapper phoneticSymbolMapper;

    @Autowired
    private SimpleCourseMapper simpleCourseMapper;

    @Resource
    private StudentExpansionMapper studentExpansionMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RunLogMapper runLogMapper;

    private static RedisTemplate<String, Object> staticRedisTemplate;

    @PostConstruct
    public void init() {
        staticRedisTemplate = redisTemplate;
    }

    /**
     * 获取摸底测试测试记录
     */
    public boolean getTestBeforeStudy(Long studentId) {
        StudentExpansion expansion = studentExpansionMapper.selectByStudentId(studentId);
        String phase = expansion.getPhase();
        Object o = redisTemplate.opsForHash().get(RedisKeysConst.TEST_BEFORE_STUDY + studentId, phase);

        if (o != null) {
            return true;
        }

        List<TestRecord> testRecords =
                testRecordMapper.selectListByGenre(studentId, GenreConstant.TEST_BEFORE_STUDY);
        for (TestRecord testRecord : testRecords) {
            String explain = testRecord.getExplain();
            if (phase.equals(explain)) {
                redisTemplate.opsForHash().put(RedisKeysConst.TEST_BEFORE_STUDY + studentId, phase, true);
                redisTemplate.expire(RedisKeysConst.TEST_BEFORE_STUDY + studentId, 30, TimeUnit.DAYS);
                return true;
            }
        }
        return false;

    }

    public boolean getGuideModel(Long studentId, String model) {
        Object o = redisTemplate.opsForHash().get(RedisKeysConst.LOOK_GUIDE, studentId);

        if (o != null && o.toString().contains(model)) {
            return false;
        }
        StudentExpansion explain = studentExpansionMapper.selectByStudentId(studentId);
        String guide = explain.getGuide();
        if (guide != null) {
            if (guide.contains(model)) {
                redisTemplate.opsForHash().put(RedisKeysConst.LOOK_GUIDE, studentId, guide);
                redisTemplate.expire(RedisKeysConst.LOOK_GUIDE + studentId, 30, TimeUnit.DAYS);
                return false;
            }
        }
        explain.setGuide(guide + "," + model);
        studentExpansionMapper.updateById(explain);
        //保存测试记录
        redisTemplate.opsForHash().put(RedisKeysConst.LOOK_GUIDE, studentId, guide);
        redisTemplate.expire(RedisKeysConst.LOOK_GUIDE + studentId, 30, TimeUnit.DAYS);
        return true;
    }

    /**
     * 获取当前学生当前模块关联的所有课程, 返回id,version
     */
    public List<Map> getCourseListInType(Long studentId, String typeStr) {
        String hKey = RedisKeysConst.ALL_COURSE_WITH_STUDENT_IN_TYPE + studentId + ":" + typeStr;
        List<Map> courseList;
        Object object = getRedisObject(hKey);
        if (object == null) {
            courseList = getCourses(studentId, typeStr);
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, hKey, courseList);
        } else {
            try {
                courseList = (List<Map>) object;
            } catch (Exception e) {
                log.error("类型转换错误, object=[{}], studentId=[{}], typeStr=[{}], error=[{}]", object, studentId, typeStr, e.getMessage());
                courseList = getCourses(studentId, typeStr);
            }
        }
        return courseList;
    }

    /**
     * 截取冲刺版字符串
     *
     * @param studentId
     * @param typeStr
     * @return
     */
    private List<Map> getCourses(Long studentId, String typeStr) {
        List<Map> courseList;
        courseList = simpleCourseMapper.getSimpleCourseByStudentIdByType(studentId, typeStr);
        if (courseList.size() > 0) {
            courseList.forEach(c -> {
                if (c.get("version") != null && c.get("version").toString().contains("冲刺版")) {
                    String[] versions = c.get("version").toString().split("-");
                    if (versions.length >= 3) {
                        c.put("version", versions[0] + " -" + versions[2]);
                    }
                }
            });
        }
        return courseList;
    }

    /**
     * 获取当前课程下各个单元的单词个数
     *
     * @param courseId
     * @return
     */
    public Map<Long, Map<Long, Object>> getWordCountWithUnitInCourse(long courseId) {
        String unitWordSumKey = RedisKeysConst.WORD_COUNT_WITH_UNIT_IN_COURSE + courseId;
        Object object = getRedisObject(unitWordSumKey);
        Map<Long, Map<Long, Object>> unitWordSum;
        if (object == null) {
            unitWordSum = simpleCourseMapper.unitsWordSum(courseId);
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, unitWordSumKey, unitWordSum);
        } else {
            try {
                unitWordSum = (Map<Long, Map<Long, Object>>) object;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}], courseId=[{}], error=[{}]", object, courseId, e.getMessage());
                unitWordSum = simpleCourseMapper.unitsWordSum(courseId);
                log.error("重新查询数据：unitWordSum=[{}]", unitWordSum);
            }
        }
        return unitWordSum;
    }


    /**
     * 获取当前单元下单词个数
     *
     * @param unitId
     * @return
     */
    public long getWordCountWithUnit(Long unitId) {
        String wordCountKey = RedisKeysConst.WORD_COUNT_WITH_UNIT + unitId;
        Object object = getRedisObject(wordCountKey);
        long wordCount;
        if (object == null) {
            wordCount = unitVocabularyMapper.countByUnitId(unitId);
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, wordCountKey, wordCount);
        } else {
            try {
                wordCount = (int) object;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}], unitId=[{}], error=[{}]", object, unitId, e.getMessage());
                wordCount = unitVocabularyMapper.countByUnitId(unitId);
                log.error("重新查询结果：wordCount=[{}]", wordCount);
            }
        }
        return wordCount;
    }

    private Object getRedisObject(String key) {
        Object object = null;
        try {
            object = redisTemplate.opsForHash().get(RedisKeysConst.PREFIX, key);
        } catch (Exception e) {
            log.error("error=[{}]", e.getMessage());
        }
        return object;
    }

    /**
     * 当前课程下所有单词数
     */
    public int wordCountInCourse(Long courseId) {
        String hKey = RedisKeysConst.WORD_COUNT_WITH_COURSE + courseId;
        Object object = getRedisHashObject(hKey);
        int count;
        if (object == null) {
            count = unitVocabularyMapper.getAllCountWordByCourse(courseId);
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, hKey, count);
        } else {
            try {
                count = (int) object;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}], courseId=[{}], error=[{}]", object, courseId, e.getMessage());
                count = unitVocabularyMapper.getAllCountWordByCourse(courseId);
                log.error("重新查询结果：count=[{}]", count);
            }
        }
        return count;
    }

    /**
     * 当前单元下所有单词信息
     *
     * @param unitId
     * @return
     */
    public List<Vocabulary> getWordInfoInUnit(Long unitId) {
        String hKey = RedisKeysConst.WORD_INFO_IN_UNIT + unitId;
        List<Vocabulary> vocabularies;
        Object object = getRedisHashObject(hKey);
        if (object == null) {
            vocabularies = vocabularyMapper.selectByUnitId(unitId);
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, hKey, vocabularies);
        } else {
            try {
                vocabularies = (List<Vocabulary>) object;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}], unitId=[{}], error=[{}]", object, unitId, e.getMessage());
                vocabularies = vocabularyMapper.selectByUnitId(unitId);
            }
        }
        return vocabularies;
    }

    public List<Vocabulary> getWordInfoInUnitAndGroup(Long unitId, Integer group) {
        String hKey = RedisKeysConst.WORD_INFO_IN_UNIT + unitId + ":" + group;
        List<Vocabulary> vocabularies;
        Object object = getRedisHashObject(hKey);
        if (object == null) {
            vocabularies = vocabularyMapper.selectByUnitIdAndGroup(unitId, group);
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, hKey, vocabularies);
        } else {
            try {
                vocabularies = (List<Vocabulary>) object;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}], unitId=[{}], error=[{}]", object, unitId, e.getMessage());
                vocabularies = vocabularyMapper.selectByUnitIdAndGroup(unitId, group);
            }
        }
        return vocabularies;
    }

    /**
     * 当前单元下所有句型信息
     *
     * @param unitId
     * @return
     */
    public List<Sentence> getSentenceInfoInUnit(Long unitId) {
        String hKey = RedisKeysConst.SENTENCE_INFO_IN_UNIT + unitId;
        List<Sentence> sentences;
        Object redisObject = getRedisHashObject(hKey);
        if (redisObject == null) {
            sentences = sentenceMapper.selectByUnitId(unitId);
        } else {
            try {
                sentences = (List<Sentence>) redisObject;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}], unitId=[{}], error=[{}]", redisObject, unitId, e.getMessage());
                sentences = sentenceMapper.selectByUnitId(unitId);
            }
        }
        return sentences;
    }

    /**
     * 获取所有等级信息
     *
     * @return
     */
    public List<Map<String, Object>> getAllLevel() {
        Object object = getRedisHashObject(RedisKeysConst.ALL_LEVEL);
        List<Map<String, Object>> allLevel;
        if (object == null) {
            allLevel = levelMapper.selectAll();
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, RedisKeysConst.ALL_LEVEL, allLevel);
        } else {
            try {
                allLevel = (List<Map<String, Object>>) object;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}]", object, e);
                allLevel = levelMapper.selectAll();
                log.error("重新查询结果：allLevel=[{}]", allLevel);
            }
        }
        return allLevel;
    }

    /**
     * 判断学生测试是不是重复提交
     *
     * @param studentId
     * @param testStartTime
     * @return true:是重复提交；false：是正常提交
     */
    public boolean isRepeatSubmit(Long studentId, Date testStartTime) {
        if (testStartTime == null) {
            return true;
        }
        String key = RedisKeysConst.TEST_SUBMIT + ":" + studentId;
        Object object = redisTemplate.opsForValue().get(key);
        redisTemplate.opsForValue().set(key, testStartTime);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
        if (object != null) {
            try {
                Date startTime = (Date) object;
                // 相同说明是重复提交的
                return Objects.equals(startTime, testStartTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取音节信息
     *
     * @return
     */
    @SuppressWarnings("all")
    public List<PhoneticSymbol> getPhoneticSymbol() {
        String key = RedisKeysConst.PHONETIC_SYMBOL;
        Object redisObject = this.getRedisStringObject(key);
        if (redisObject == null) {
            return getPhoneticSymbols(key);
        } else {
            try {
                return (List<PhoneticSymbol>) redisObject;
            } catch (Exception e) {
                return getPhoneticSymbols(key);
            }
        }
    }

    private List<PhoneticSymbol> getPhoneticSymbols(String key) {
        List<PhoneticSymbol> phoneticSymbols = phoneticSymbolMapper.selectList(null);
        redisTemplate.opsForValue().set(key, phoneticSymbols);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
        return phoneticSymbols;
    }

    private Object getRedisStringObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public static Map<String, Object> getSessionMap(String sessionId) {
        Object object = staticRedisTemplate.opsForHash().get(RedisKeysConst.SESSION_MAP, sessionId);
        if (object == null) {
            return null;
        }
        return (Map<String, Object>) object;
    }

    private Object getRedisHashObject(String key) {
        Object object = null;
        try {
            object = redisTemplate.opsForHash().get(RedisKeysConst.PREFIX, key);
        } catch (Exception e) {
            log.error("error=[{}]", e.getMessage());
        }
        return object;
    }

    /**
     * 判断学生是否是第一次登录系统
     *
     * @param studentId
     * @return true：是第一次登录系统；false：不是第一次登录系统
     */
    public boolean firstLogin(Long studentId) {
        Object object = redisTemplate.opsForHash().get(RedisKeysConst.FIRST_LOGIN, studentId);
        if (object != null) {
            return false;
        }
        Integer count = runLogMapper.selectLoginCountByStudentId(studentId);
        boolean flag = count == 0;
        redisTemplate.opsForHash().put(RedisKeysConst.FIRST_LOGIN, studentId, flag);
        redisTemplate.expire(RedisKeysConst.FIRST_LOGIN, 7, TimeUnit.DAYS);
        return flag;
    }

    /**
     * 将当前 sessionId 标记为异地登录被挤掉的 sessionId，在 session 失效时不保存该 session 的时长信息
     *
     * @param oldSessionId
     */
    public void markMultipleLoginSessionId(String oldSessionId) {
        try {
            String key = RedisKeysConst.MULTIPLE_LOGIN_SESSION_ID + oldSessionId;
            redisTemplate.opsForValue().set(key, oldSessionId);
            redisTemplate.expire(key, 40, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("标记 oldSessionId 出错！oldSessionId=[{}]", oldSessionId, e);
        }
    }

}
