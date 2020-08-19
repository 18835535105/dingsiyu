package com.zhidejiaoyu.common.utils.goldUtil;

import com.zhidejiaoyu.common.mapper.StudentExpansionMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 判断当日闯关类金币是否可以继续增加工具类
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
    private static final int MAX_GOLD = 500;

    /**
     * 小程序每天最多获取金币数
     */
    private static final int SMALL_APP_MAX_GOLD = 100;

    @Resource
    private StudentExpansionMapper studentExpansionMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static StudentExpansionMapper studentExpansionMapperStatic;

    private static StudentMapper studentMapperStatic;

    private static RedisTemplate<String, Object> redisTemplateStatic;


    @PostConstruct
    public void init() {
        redisTemplateStatic = this.redisTemplate;
        studentMapperStatic = this.studentMapper;
        studentExpansionMapperStatic = this.studentExpansionMapper;
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
        return addStudentGold(student, Integer.parseInt(gold.toString()));
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
        Long studentId = student.getId();
        int addGold = 0;
        StudentExpansion studentExpansion = studentExpansionMapperStatic.selectByStudentId(studentId);
        if (studentExpansion == null) {
            studentExpansion = new StudentExpansion();
            studentExpansion.setTestGoldAdd(gold);
            studentExpansion.setStudentId(studentId);
            addGold = gold;
            insertStudentExpansion(studentExpansion, student);
            return addGold;
        }

        if (studentExpansion.getTestGoldAdd() == null) {
            studentExpansion.setTestGoldAdd(gold);
            updateStudentExpansion(studentExpansion, student);
            return gold;
        }

        if (studentExpansion.getTestGoldAdd() < MAX_GOLD) {
            // 当日闯关获得金币加上当前测试获得金币大于最大值，取差值
            if (studentExpansion.getTestGoldAdd() + gold > MAX_GOLD) {
                addGold = MAX_GOLD - studentExpansion.getTestGoldAdd();
                studentExpansion.setTestGoldAdd(MAX_GOLD);
            } else {
                // 不足最大值直接加上当前测试获得金币数
                addGold = gold;
                studentExpansion.setTestGoldAdd(studentExpansion.getTestGoldAdd() + gold);
            }
            updateStudentExpansion(studentExpansion, student);
            return addGold;
        }

        log.info("学生[{}]->[{}] 今日闯关类测试获取金币数=300", student.getId(), student.getStudentName());
        return addGold;
    }

    /**
     * 可奖励金币个数
     *
     * @param student
     * @param gold    本次可获取的金币数
     * @return
     */
    public static int canAddGold(Student student, Double gold) {
        return 0;
    }

    private static void updateStudentExpansion(StudentExpansion studentExpansion, Student student) {
        try {
            studentExpansionMapperStatic.updateById(studentExpansion);
        } catch (Exception e) {
            log.error("闯关类每日最多增加金币量更新学生扩展信息失败，[{}]->[{}]", student.getId(), student.getStudentName(), e);
        }
    }

    private static void insertStudentExpansion(StudentExpansion studentExpansion, Student student) {
        try {
            studentExpansionMapperStatic.insert(studentExpansion);
        } catch (Exception e) {
            log.error("闯关类每日最多增加金币量新增学生扩展信息失败，[{}]->[{}]", student.getId(), student.getStudentName(), e);
        }
    }
}
