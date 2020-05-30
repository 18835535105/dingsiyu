package com.zhidejiaoyu.student.common.redis;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.PkCopyBaseMapper;
import com.zhidejiaoyu.common.pojo.PkCopyBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 副本挑战相关缓存
 *
 * @author: wuchenxi
 * @date: 2020/3/18 10:00:00
 */
@Slf4j
@Component
public class PkCopyRedisOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private PkCopyBaseMapper pkCopyBaseMapper;

    /**
     * 记录参加校区指定副本挑战的学生id
     *
     * @param schoolAdminId
     * @param pkCopyBaseId
     * @param studentId
     */
    public void saveSchoolCopyStudentInfo(Integer schoolAdminId, Long pkCopyBaseId, Long studentId) {
        String key = RedisKeysConst.SCHOOL_COPY + schoolAdminId;
        Object o = redisTemplate.opsForHash().get(key, pkCopyBaseId);
        Set<Long> studentIds;
        if (o == null) {
            studentIds = new HashSet<>();
        } else {
            studentIds = (Set<Long>) o;
        }
        studentIds.add(studentId);
        redisTemplate.opsForHash().put(key, pkCopyBaseId, studentIds);
        redisTemplate.expire(key, 2, TimeUnit.DAYS);
    }

    /**
     * 获取参加校区指定副本挑战的学生id
     *
     * @param schoolAdminId
     * @param pkCopyBaseId
     * @return
     */
    public Set<Long> getSchoolCopyStudentInfo(Integer schoolAdminId, Long pkCopyBaseId) {
        String key = RedisKeysConst.SCHOOL_COPY + schoolAdminId;
        Object o = redisTemplate.opsForHash().get(key, pkCopyBaseId);
        if (o != null) {
            return (Set<Long>) o;
        }
        return new HashSet<>();
    }

    /**
     * 标记当前校区副本已挑战成功
     *
     * @param schoolAdminId 校管id
     * @param pkCopyBaseId  副本id
     * @param durability
     * @return
     */
    public void markSchoolCopyAward(Integer schoolAdminId, Long pkCopyBaseId, Integer durability) {
        String key = RedisKeysConst.SCHOOL_COPY_AWARD_MARK + schoolAdminId;
        redisTemplate.opsForHash().put(key, pkCopyBaseId, durability);
        redisTemplate.expire(key, 2, TimeUnit.DAYS);
    }

    /**
     * 判断当前校区副本是否已挑战成功
     *
     * @param schoolAdminId
     * @param pkCopyBaseId
     * @return <ul>
     * <li>true:已挑战成功，金币奖励已发放</li>
     * <li>false：未挑战成功，如果挑战成功可以发放金币奖励</li>
     * </ul>
     */
    public boolean judgeSchoolCopyAward(Integer schoolAdminId, Long pkCopyBaseId) {
        String key = RedisKeysConst.SCHOOL_COPY_AWARD_MARK + schoolAdminId;
        Object o = redisTemplate.opsForHash().get(key, pkCopyBaseId);
        return o != null && Integer.parseInt(o.toString()) <= 0;
    }

    /**
     * 获取校区副本剩余耐久度
     *
     * @param schoolAdminId
     * @param pkCopyBaseId
     * @return
     */
    public Integer getSchoolCopySurplusDurability(Integer schoolAdminId, Long pkCopyBaseId) {
        String key = RedisKeysConst.SCHOOL_COPY_AWARD_MARK + schoolAdminId;
        Object o = redisTemplate.opsForHash().get(key, pkCopyBaseId);
        return o == null ? null : Integer.parseInt(o.toString());
    }

    /**
     * 根据副本id获取副本信息
     *
     * @param pkCopyBaseId 副本id
     * @return
     */
    public PkCopyBase getPkCopyBaseById(Long pkCopyBaseId) {
        String key = RedisKeysConst.PK_COPY_BASE;
        Object o = redisTemplate.opsForHash().get(key, pkCopyBaseId);
        if (o == null) {
            List<PkCopyBase> pkCopyBases = pkCopyBaseMapper.selectList(null);
            for (PkCopyBase pkCopyBase : pkCopyBases) {
                redisTemplate.opsForHash().put(key, pkCopyBase.getId(), pkCopyBase);
            }
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
            return (PkCopyBase) redisTemplate.opsForHash().get(key, pkCopyBaseId);
        }
        return (PkCopyBase) o;
    }

    /**
     * 获取校区可挑战的副本id
     *
     * @param schoolAdminId
     * @return
     */
    public Long getSchoolBossIdBySchoolAdminId(Integer schoolAdminId) {
        String key = RedisKeysConst.SCHOOL_PK_BASE_INFO;
        Object o = redisTemplate.opsForHash().get(key, schoolAdminId);
        return o == null ? null : Long.parseLong(o.toString());
    }

    /**
     * 存储校区可挑战的副本id
     *
     * @param schoolAdminId
     * @param bossId
     */
    public void saveCanSchoolBossId(Integer schoolAdminId, Long bossId) {
        String key = RedisKeysConst.SCHOOL_PK_BASE_INFO;
        redisTemplate.opsForHash().put(key, schoolAdminId, bossId);
        redisTemplate.expire(key, 2, TimeUnit.DAYS);
    }
}
