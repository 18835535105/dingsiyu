package com.zhidejiaoyu.student.common.redis;


import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.student.business.feignclient.course.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 学生当日飞行记录详情
 */
@Slf4j
@Component
public class CurrentDayOfStudyRedisOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UnitFeignClient unitFeignClient;
    @Resource
    private VocabularyFeignClient vocabularyFeignClient;
    @Resource
    private SentenceFeignClient sentenceFeignClient;
    @Resource
    private SyntaxTopicFeignClient syntaxTopicFeignClient;


    /**
     * 保存正常学习下的错误信息
     *
     * @param redisStr  redis数据
     * @param studentId 学生id
     * @param feldId    学习数据id
     */
    public void saveStudyCurrent(String redisStr, Long studentId, Long feldId) {
        Object o = redisTemplate.opsForHash().get(redisStr + studentId, feldId);
        if (o != null) {
            int i = Integer.parseInt(o.toString());
            redisTemplate.opsForHash().put(redisStr + studentId, feldId, i);
        } else {
            redisTemplate.opsForHash().put(redisStr + studentId, feldId, 1);
        }
    }

    public void deleteStudy(Long studentId) {
        this.deleteTest(studentId);
        this.deleteSyntaxAndSentence(RedisKeysConst.ERROR_WORD, studentId);
        this.deleteSyntaxAndSentence(RedisKeysConst.ERROR_SYNTAX, studentId);
        this.deleteSyntaxAndSentence(RedisKeysConst.ERROR_SENTENCE, studentId);
        this.deleteStudyModel(studentId);
    }

    private void deleteTest(Long studentId) {
        redisTemplate.opsForHash().delete(RedisKeysConst.ERROR_TEST + studentId, 1);
    }

    private void deleteStudyModel(Long studentId) {
        redisTemplate.opsForHash().delete(RedisKeysConst.STUDY_MODEL + studentId, 1);
    }

    private void deleteSyntaxAndSentence(String redisStr, Long studentId) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(redisStr + studentId);
        Set<Object> objects = entries.keySet();
        objects.forEach(key -> {
            Integer integer = Integer.parseInt(key.toString());
            redisTemplate.opsForHash().delete(redisStr + studentId, integer);
        });
    }

    public void saveStudyModel(Long studentId, String studyModel, Long unitId) {
        UnitNew unitNew = unitFeignClient.selectById(unitId);
        studyModel += "-" + unitNew.getJointName();
        Object o = redisTemplate.opsForHash().get(RedisKeysConst.STUDY_MODEL + studentId, 1);
        if (o == null) {
            redisTemplate.opsForHash().put(RedisKeysConst.STUDY_MODEL + studentId, 1, studyModel);
        } else {
            String study = o.toString() + "##" + studyModel;
            redisTemplate.opsForHash().put(RedisKeysConst.STUDY_MODEL + studentId, 1, study);
        }
    }

    public String getStudyModelAndTestStudyCurrent(String redisStr, Long studentId) {
        Object o = redisTemplate.opsForHash().get(redisStr + studentId, 1);
        return o == null ? "" : o.toString();
    }

    /**
     * 保存测试下的错误信息
     */
    public void saveTestStudyCurrent(Long studentId, String errorTestInfo) {
        if (errorTestInfo != null && errorTestInfo.length() > 0) {
            Object o = redisTemplate.opsForHash().get(RedisKeysConst.ERROR_TEST + studentId, 1);
            String errorTestInfo1;
            if (o == null) {
                errorTestInfo1 = getErrorTestInfo(errorTestInfo, null);

            } else {
                String testInfo = o.toString();
                errorTestInfo1 = getErrorTestInfo(errorTestInfo, testInfo);
            }
            redisTemplate.opsForHash().put(RedisKeysConst.ERROR_TEST + studentId, 1, errorTestInfo1);
        }
    }

    private String getErrorTestInfo(String errorTestInfo, String testInfo) {
        if (testInfo == null) {
            testInfo = new String();
        }
        StringBuilder builder = new StringBuilder(testInfo);
        String[] split = errorTestInfo.split("##");
        for (String str : split) {
            testInfo = builder.toString();
            if (!testInfo.contains(str)) {
                String[] suStr = str.split("&&");
                if (suStr.length > 0) {
                    if (suStr.length > 1) {
                        builder.append(suStr[0]).append("&&").append(suStr[1]).append("##");
                    } else {
                        builder.append(suStr[0]).append("##");
                    }
                }
            }
        }
        return builder.toString();

    }

    public List<Map<String, Object>> getWordSentenceTest(String redisStr, Long studentId, Integer type) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(redisStr + studentId);
        Set<Object> objects = entries.keySet();
        List<Map<String, Object>> returnMap = new ArrayList<>();
        objects.forEach(key -> {
            Integer integer = Integer.parseInt(key.toString());
            if (integer != null && integer > 3) {
                if (type.equals(1)) {
                    Vocabulary vocabulary = vocabularyFeignClient.selectVocabularyById(integer.longValue());
                    if (vocabulary != null) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("english", vocabulary.getWord());
                        map.put("chinese", vocabulary.getWordChinese());
                        returnMap.add(map);
                    }
                }
                if (type.equals(2)) {
                    Sentence sentence = sentenceFeignClient.selectSentenceById(integer.longValue());
                    if (sentence != null) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("english", sentence.getCentreExample().replace("#", " ").replace("$", ""));
                        map.put("chinese", sentence.getCentreTranslate().replace("*", ""));
                        returnMap.add(map);
                    }
                }

            }
        });
        return returnMap;
    }


    /**
     * 查询数据
     *
     * @param redisStr  数据格式
     * @param studentId 学生id
     * @param type      类型1单词 2句型 3语法
     */
    public String getTestStudyCurrent(String redisStr, Long studentId, Integer type) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(redisStr + studentId);
        Set<Object> objects = entries.keySet();
        StringBuilder stringBuilder = new StringBuilder();
        objects.forEach(key -> {
            Integer integer = Integer.parseInt(key.toString());
            if (integer != null && integer > 3) {
                if (type.equals(1)) {
                    Vocabulary vocabulary = vocabularyFeignClient.selectVocabularyById(integer.longValue());
                    if (vocabulary != null) {
                        stringBuilder.append(vocabulary.getWord()).append("##");
                    }
                }
                if (type.equals(2)) {
                    Sentence sentence = sentenceFeignClient.selectSentenceById(integer.longValue());
                    if (sentence != null) {
                        stringBuilder.append(sentence.getCentreExample().replace("#", " ").replace("$", "")).append("##");
                    }
                }
                if (type.equals(3)) {
                    try{
                        SyntaxTopic syntaxTopic = syntaxTopicFeignClient.selectSyntaxTopicById(integer.longValue());
                        if (syntaxTopic != null) {
                            stringBuilder.append(syntaxTopic.getTopic() + ":" + syntaxTopic.getAnswer()).append("##");
                        }
                    }catch (Exception e){

                    }

                }
            }
        });
        return stringBuilder.toString();

    }
}
