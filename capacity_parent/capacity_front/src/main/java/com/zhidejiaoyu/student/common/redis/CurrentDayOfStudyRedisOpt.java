package com.zhidejiaoyu.student.common.redis;


import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.SentenceMapper;
import com.zhidejiaoyu.common.mapper.SyntaxTopicMapper;
import com.zhidejiaoyu.common.mapper.UnitNewMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.common.pojo.UnitNew;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class CurrentDayOfStudyRedisOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private VocabularyMapper vocabularyMapper;
    @Resource
    private SentenceMapper sentenceMapper;
    @Resource
    private SyntaxTopicMapper syntaxTopicMapper;
    @Resource
    private UnitNewMapper unitNewMapper;


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
        redisTemplate.opsForHash().delete(RedisKeysConst.ERROR_TEST + studentId);
        redisTemplate.opsForHash().delete(RedisKeysConst.ERROR_SYNTAX + studentId);
        redisTemplate.opsForHash().delete(RedisKeysConst.ERROR_SENTENCE + studentId);
        redisTemplate.opsForHash().delete(RedisKeysConst.ERROR_TEKS + studentId);
        redisTemplate.opsForHash().delete(RedisKeysConst.ERROR_WORD + studentId);
        redisTemplate.opsForHash().delete(RedisKeysConst.STUDY_MODEL + studentId);
    }

    public void saveStudyModel(Long studentId, String studyModel, Long unitId) {
        UnitNew unitNew = unitNewMapper.selectById(unitId);
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
        return redisTemplate.opsForHash().get(redisStr + studentId, 1).toString();
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
        StringBuilder builder = new StringBuilder();
        String[] split = errorTestInfo.split("##");
        for (String str : split) {
            String[] suStr = str.split("&&");
            if (suStr.length > 0) {
                if (suStr.length > 1) {
                    builder.append(suStr[0]).append("&&").append(suStr[1]).append("##");
                } else {
                    builder.append(suStr[0]).append("##");
                }
            }
        }
        if (testInfo == null) {
            testInfo = builder.toString();
        } else {
            testInfo += builder.toString();
        }
        return testInfo;

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
            Object o = entries.get(key);
            if (o != null) {
                Integer integer = Integer.getInteger(o.toString());
                if (integer != null && integer > 3) {
                    if (type.equals(1)) {
                        Vocabulary vocabulary = vocabularyMapper.selectById(integer);
                        if (vocabulary != null) {
                            stringBuilder.append(vocabulary.getWord()).append("##");
                        }
                    }
                    if (type.equals(2)) {
                        Sentence sentence = sentenceMapper.selectById(integer);
                        if (sentence != null) {
                            stringBuilder.append(sentence.getCentreExample().replace("#", " ").replace("$", "")).append("##");
                        }
                    }
                    if (type.equals(3)) {
                        SyntaxTopic syntaxTopic = syntaxTopicMapper.selectById(integer);
                        if (syntaxTopic != null) {
                            stringBuilder.append(syntaxTopic.getTopic() + ":" + syntaxTopic.getAnswer()).append("##");
                        }
                    }
                }
            }
        });
        return stringBuilder.toString();

    }
}
