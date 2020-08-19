package com.zhidejiaoyu.common.utils.goldUtil;

import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 判断当日金币是否可以继续增加工具类
 *
 * @author wuchenxi
 * @date 2019-03-27
 */
@Slf4j
@Component
public class GoldUtil {

    /**
     * 学生每天最多可获取金币数
     */
    private static final int MAX_GOLD = 400;

    /**
     * 小程序每天最多获取金币数
     */
    private static final int SMALL_APP_MAX_GOLD = 100;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static StudentMapper studentMapperStatic;

    private static RedisTemplate<String, Object> redisTemplateStatic;

    @PostConstruct
    public void init() {
        redisTemplateStatic = this.redisTemplate;
        studentMapperStatic = this.studentMapper;
    }

    /**
     * 增加学生金币
     *
     * @param student
     * @param gold
     * @return 学生增加的金币数
     */
    public static int addStudentGold(Student student, Integer gold) {
        if (gold == null) {
            return 0;
        }
        int canAddGold = canAddGold(student, gold);
        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), canAddGold));
        studentMapperStatic.updateById(student);
        return canAddGold;
    }

    /**
     * 学生微信小程序获取金币
     *
     * @param student
     * @param gold
     * @return 学生增加的金币数
     */
    public static int addSmallAppGold(Student student, Integer gold) {
        if (gold == null) {
            return 0;
        }
        int canAddGold = canSmallAppAddGold(student, gold);
        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), canAddGold));
        studentMapperStatic.updateById(student);
        return canAddGold;
    }

    /**
     * 增加学生金币
     *
     * @param student
     * @param gold
     * @return 学生增加的金币数
     */
    public static int addStudentGold(Student student, Double gold) {
        if (gold == null) {
            return 0;
        }
        return addStudentGold(student, (int) Math.floor(gold));
    }

    /**
     * 可奖励金币个数
     *
     * @param student
     * @param gold    本次可获取的金币数
     * @return
     */
    public static int canAddGold(Student student, int gold) {
        if (gold == 0) {
            return 0;
        }

        Object o = redisTemplateStatic.opsForHash().get(RedisKeysConst.STUDENT_DAY_TOTAL_GOLD, student.getId());
        if (o == null) {
            int min = Math.min(MAX_GOLD, gold);
            saveCacheGold(student, min, RedisKeysConst.STUDENT_DAY_TOTAL_GOLD);
            return min;
        }

        int todayTotalGold = (int) o;
        if (todayTotalGold >= MAX_GOLD) {
            log.info("学生[{}]->[{}] 今日获取总金币数={}", student.getId(), student.getStudentName(), MAX_GOLD);
            return 0;
        }

        int min = Math.abs(Math.min(MAX_GOLD - todayTotalGold, gold));
        saveCacheGold(student, min + todayTotalGold, RedisKeysConst.STUDENT_DAY_TOTAL_GOLD);
        return min;
    }

    /**
     * @param student
     * @param gold    可获得的金币数
     * @param key
     */
    private static void saveCacheGold(Student student, int gold, String key) {
        redisTemplateStatic.opsForHash().put(key, student.getId(), gold);
        redisTemplateStatic.expire(key, 1, TimeUnit.DAYS);
    }

    /**
     * 小程序今天还能获取的金币数
     *
     * @param student
     * @param gold
     * @return
     */
    public static int canSmallAppAddGold(Student student, int gold) {
        if (gold == 0) {
            return 0;
        }

        Object o = redisTemplateStatic.opsForHash().get(RedisKeysConst.STUDENT_SMALL_APP_DAY_TOTAL_GOLD, student.getId());

        int canAddGold;
        if (o == null) {
            canAddGold = Math.min(SMALL_APP_MAX_GOLD, gold);
            saveCacheGold(student, canAddGold, RedisKeysConst.STUDENT_SMALL_APP_DAY_TOTAL_GOLD);
        } else {
            int todaySmallAppGold = (int) o;
            canAddGold = Math.abs(Math.min(SMALL_APP_MAX_GOLD - todaySmallAppGold, gold));
            saveCacheGold(student, canAddGold + todaySmallAppGold, RedisKeysConst.STUDENT_SMALL_APP_DAY_TOTAL_GOLD);
        }

        if (canAddGold == 0) {
            log.info("学生[{}]->[{}] 今日从小程序获取金币数={}", student.getId(), student.getStudentName(), SMALL_APP_MAX_GOLD);
        }

        return canAddGold(student, canAddGold);
    }

}
