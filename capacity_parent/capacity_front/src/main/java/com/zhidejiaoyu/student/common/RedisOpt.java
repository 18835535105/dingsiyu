package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private CourseMapper courseMapper;

    @Autowired
    private StudentUnitMapper studentUnitMapper;

    @Autowired
    private LevelMapper levelMapper;

    @Autowired
    private SentenceMapper sentenceMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 当前课程下所有单词数
     */
    public int wordCountInCourse(Long courseId) {
        String hKey = RedisKeysConst.WORD_COUNT_WITH_COURSE + courseId;
        Object object = getRedisObject(hKey);
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
        Object object = getRedisObject(hKey);
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

    /**
     * 当前单元下所有句型信息
     *
     * @param unitId
     * @return
     */
    public List<Sentence> getSentenceInfoInUnit(Long unitId) {
        String hKey = RedisKeysConst.SENTENCE_INFO_IN_UNIT + unitId;
        List<Sentence> sentences;
        Object redisObject = getRedisObject(hKey);
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
        Object object = getRedisObject(RedisKeysConst.ALL_LEVEL);
        List<Map<String, Object>> allLevel;
        if (object == null) {
            allLevel = levelMapper.selectAll();
            redisTemplate.opsForHash().put(RedisKeysConst.PREFIX, RedisKeysConst.ALL_LEVEL, allLevel);
        } else {
            try{
                allLevel = (List<Map<String, Object>>) object;
            } catch (Exception e) {
                log.error("类型转换错误，object=[{}]", object, e.getMessage());
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

    private Object getRedisObject(String key) {
        Object object = null;
        try {
            object = redisTemplate.opsForHash().get(RedisKeysConst.PREFIX, key);
        } catch (Exception e) {
            log.error("error=[{}]", e.getMessage());
        }
        return object;
    }
}
