package com.zhidejiaoyu.student.common.redis;

import com.zhidejiaoyu.common.constant.redis.SyntaxKeysConst;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.common.mapper.KnowledgePointMapper;
import com.zhidejiaoyu.common.mapper.SyntaxTopicMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 语法缓存操作
 *
 * @author: wuchenxi
 * @Date: 2019/10/31 10:12
 */
@Slf4j
@Component
public class SyntaxRedisOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private SyntaxTopicMapper syntaxTopicMapper;

    @Resource
    private KnowledgePointMapper knowledgePointMapper;


    /**
     * 获取当前单元所有语法知识点个数
     *
     * @param unitId
     * @return
     */
    public int getTotalKnowledgePointWithUnitId(Long unitId) {
        String key = SyntaxKeysConst.KNOWLEDGE_POINT_COUNT_WITH_UNIT + unitId ;
        Object o = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(o)) {
            return getTotalKnowledgePointWithUnitId(unitId, key);
        }

        try {
            return Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            log.warn("缓存数据转换错误", e);
            return getTotalKnowledgePointWithUnitId(unitId, key);
        }
    }

    /**
     * 获取语法单元中学习内容个数
     *
     * @param unitId
     * @return
     */
    public int getTotalSyntaxContentWithUnitId(Long unitId, String studyModel) {
        String key = SyntaxKeysConst.SYNTAX_CONTENT_COUNT_WITH_UNIT + unitId +  ":" + studyModel;
        Object o = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(o)) {
            return this.getTotalSyntaxContentWithUnitId(unitId, studyModel, key);
        }

        try {
            return Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            log.warn("缓存数据转换错误", e);
            return this.getTotalSyntaxContentWithUnitId(unitId, studyModel, key);
        }
    }

    /**
     * 获取当前单元所有语法知识点个数
     *
     * @param unitId
     * @param key
     * @return
     */
    private int getTotalKnowledgePointWithUnitId(Long unitId, String key) {
        int count = knowledgePointMapper.countByUnitId(unitId);
        redisTemplate.opsForValue().set(key, count);
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
        return count;
    }

    /**
     * 查询当前单元指定模块的语法内容个数
     *
     * @param unitId
     * @param studyModel
     * @param key
     * @return
     */
    private int getTotalSyntaxContentWithUnitId(Long unitId, String studyModel, String key) {
        Integer count = syntaxTopicMapper.countByUnitIdAndType(unitId, Objects.equals(studyModel, SyntaxModelNameConstant.SELECT_SYNTAX) ? 1 : 2);
        int returnCount = Objects.isNull(count) ? 0 : count;
        redisTemplate.opsForValue().set(key, returnCount);
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
        return returnCount;
    }

}
