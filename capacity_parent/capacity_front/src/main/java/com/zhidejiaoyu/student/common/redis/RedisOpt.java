package com.zhidejiaoyu.student.common.redis;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.student.business.feignclient.course.CourseFeignClient;
import com.zhidejiaoyu.student.business.feignclient.course.SentenceFeignClient;
import com.zhidejiaoyu.student.business.feignclient.course.VocabularyFeignClient;
import com.zhidejiaoyu.student.business.shipconfig.service.impl.ShipAddEquipmentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
    private LevelMapper levelMapper;

    @Autowired
    private PhoneticSymbolMapper phoneticSymbolMapper;

    @Resource
    private SentenceFeignClient sentenceFeignClient;

    private final CourseFeignClient courseFeignClient;

    @Resource
    private VocabularyFeignClient vocabularyFeignClient;

    @Resource
    private StudentExpansionMapper studentExpansionMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StudentEquipmentMapper studentEquipmentMapper;

    private static RedisTemplate<String, Object> staticRedisTemplate;

    public RedisOpt(CourseFeignClient courseFeignClient) {
        this.courseFeignClient = courseFeignClient;
    }

    @PostConstruct
    public void init() {
        staticRedisTemplate = redisTemplate;
    }

    /**
     * 获取摸底测试测试记录
     */
    public boolean getTestBeforeStudy(Long studentId) {
        StudentExpansion expansion = studentExpansionMapper.selectByStudentId(studentId);
        String phase;
        if (StringUtils.isEmpty(expansion.getPhase())) {
            expansion.setPhase("小学");
            studentExpansionMapper.updateById(expansion);
            phase = "小学";
        } else {
            phase = expansion.getPhase();
        }

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
    public List<Map<String, Object>> getCourseListWithPhase(Long studentId, String phase) {
        String hKey = RedisKeysConst.ALL_COURSE_WITH_STUDENT_IN_TYPE + studentId + ":" + phase;
        List<Map<String, Object>> courseList;
        Object object = getRedisObject(hKey);
        if (object == null || object.toString().length() <= 2) {
            courseList = getCourses(studentId, phase);
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, hKey, courseList);
        } else {
            try {
                courseList = (List<Map<String, Object>>) object;
                if (CollectionUtils.isEmpty(courseList)) {
                    courseList = getCourses(studentId, phase);
                }
            } catch (Exception e) {
                log.error("类型转换错误, object=[{}], studentId=[{}], typeStr=[{}], error=[{}]", object, studentId, phase, e.getMessage());
                courseList = getCourses(studentId, phase);
            }
        }
        return courseList;
    }

    /**
     * 截取冲刺版字符串
     *
     * @param studentId
     * @param phase
     * @return
     */
    private List<Map<String, Object>> getCourses(Long studentId, String phase) {
        List<Map<String, Object>> courseList = courseFeignClient.selectIdAndVersionByStudentIdByPhase(studentId, phase);
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
    public Map<Long, Map<String, Object>> getWordCountWithUnitInCourse(long courseId) {
        String unitWordSumKey = RedisKeysConst.WORD_COUNT_WITH_UNIT_IN_COURSE + courseId;
        Object object = getRedisObject(unitWordSumKey);
        Map<Long, Map<String, Object>> unitWordSum;
        if (object == null) {
            unitWordSum = courseFeignClient.selectUnitsWordSum(courseId);
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, unitWordSumKey, unitWordSum);
        } else {
            try {
                //unitWordSum = (Map<Long, Map<String, Object>>) object;
                unitWordSum =  courseFeignClient.selectUnitsWordSum(courseId);
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}], courseId=[{}], error=[{}]", object, courseId, e.getMessage());
                unitWordSum = courseFeignClient.selectUnitsWordSum(courseId);
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
            wordCount = vocabularyFeignClient.countByUnitId(unitId);
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, wordCountKey, wordCount);
        } else {
            try {
                wordCount = (int) object;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}], unitId=[{}], error=[{}]", object, unitId, e.getMessage());
                wordCount = vocabularyFeignClient.countByUnitId(unitId);
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
            count = vocabularyFeignClient.countAllCountWordByCourse(courseId);
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, hKey, count);
        } else {
            try {
                count = (int) object;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}], courseId=[{}], error=[{}]", object, courseId, e.getMessage());
                count = vocabularyFeignClient.countAllCountWordByCourse(courseId);
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
            vocabularies = vocabularyFeignClient.selectByUnitId(unitId);
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, hKey, vocabularies);
        } else {
            try {
                vocabularies = (List<Vocabulary>) object;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}], unitId=[{}], error=[{}]", object, unitId, e.getMessage());
                vocabularies = vocabularyFeignClient.selectByUnitId(unitId);
            }
        }
        return vocabularies;
    }

    /**
     * 当前单元下所有单词信息
     *
     * @param unitId
     * @return
     */
    public List<Vocabulary> getVoiceInfoInUnit(Long unitId, Integer group) {
        String hKey = RedisKeysConst.WORD_INFO_IN_UNIT + unitId + " " + RedisKeysConst.WORD_INFO_IN_UNIT_GROUP + group;
        List<Vocabulary> vocabularies;
        Object object = getRedisHashObject(hKey);
        if (object == null) {
            vocabularies = vocabularyFeignClient.selectByUnitIdAndGroup(unitId, group);
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, hKey, vocabularies);
        } else {
            try {
                vocabularies = (List<Vocabulary>) object;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}], unitId=[{}], error=[{}]", object, unitId, e.getMessage());
                vocabularies = vocabularyFeignClient.selectByUnitIdAndGroup(unitId, group);
            }
        }
        return vocabularies;
    }

    public List<Vocabulary> getWordInfoInUnitAndGroup(Long unitId, Integer group) {
        String hKey = RedisKeysConst.WORD_INFO_IN_UNIT + unitId + ":" + group;
        List<Vocabulary> vocabularies;
        Object object = getRedisHashObject(hKey);
        if (object == null) {
            vocabularies = vocabularyFeignClient.selectByUnitIdAndGroup(unitId, group);
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, hKey, vocabularies);
        } else {
            try {
                vocabularies = (List<Vocabulary>) object;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}], unitId=[{}], error=[{}]", object, unitId, e.getMessage());
                vocabularies = vocabularyFeignClient.selectByUnitIdAndGroup(unitId, group);
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
            sentences = sentenceFeignClient.selectByUnitId(unitId);
        } else {
            try {
                sentences = (List<Sentence>) redisObject;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}], unitId=[{}], error=[{}]", redisObject, unitId, e.getMessage());
                sentences = sentenceFeignClient.selectByUnitId(unitId);
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

    /**
     * 如果单词《飞船建造》游戏得分小于80分，记录单元group
     *
     * @param studentId
     * @param unitId
     * @param group
     */
    public void saveFirstFalseAdd(Long studentId, Long unitId, Integer group) {
        String k = RedisKeysConst.FIRST_FALSE_ADD + ":" + studentId + ":" + unitId + ":" + group;
        redisTemplate.opsForValue().set(k, true);
        redisTemplate.expire(k, 30, TimeUnit.DAYS);
    }

    /**
     * 清除单词首次错误记忆强度增加50%标识
     *
     * @param studentId
     * @param unitId
     * @param group
     */
    public void clearFirstFalseAdd(Long studentId, Long unitId, Integer group) {
        String k = RedisKeysConst.FIRST_FALSE_ADD + ":" + studentId + ":" + unitId + ":" + group;
        redisTemplate.opsForValue().set(k, false);
        redisTemplate.expire(k, 10, TimeUnit.SECONDS);
    }

    /**
     * 获取单词首次错误记忆强度增加50%标识
     *
     * @param studentId
     * @param unitId
     * @param group
     * @return true:当前group单词首次答错需要额外增加50%的记忆强度
     */
    public boolean getFirstFalseAdd(Long studentId, Long unitId, Integer group) {
        String k = RedisKeysConst.FIRST_FALSE_ADD + ":" + studentId + ":" + unitId + ":" + group;
        Object o = redisTemplate.opsForValue().get(k);
        if (o == null) {
            return false;
        }

        return (boolean) o;
    }

    /**
     * 判断是否已初始化登录即可领取的飞船，如果未初始化，进行初始化
     *
     * @param studentId
     */
    public void initShip(Long studentId) {
        String key = RedisKeysConst.INIT_SHIP + studentId;
        Object o = redisTemplate.opsForValue().get(key);
        if (o == null) {
            int count = studentEquipmentMapper.countByStudentId(studentId);
            if (count == 0) {
                List<Long> equipmentIds = new ArrayList<>();
                equipmentIds.add(1L);
                equipmentIds.add(2L);
                equipmentIds.add(3L);
                ShipAddEquipmentServiceImpl.addEquipment(equipmentIds, studentId, studentEquipmentMapper);
            }
            redisTemplate.opsForValue().set(key, true);
            redisTemplate.expire(key, 15, TimeUnit.DAYS);
        }
    }
}
