package com.zhidejiaoyu.common.utils.goldUtil;


import com.zhidejiaoyu.common.pojo.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 计算金币加成工具类
 */
@Slf4j
@Component
public class StudentGoldAdditionUtil {

    private static final double GLOVESADDITION = 1.2;

    /**
     * 获取加成后的金币数
     *
     * @param student
     * @param gold
     * @return
     */
    public static Double getGoldAddition(Student student, Double gold) {
        Date date = new Date();
        if (student.getBonusExpires() != null && student.getBonusExpires().getTime() >= date.getTime()) {
            return gold * GLOVESADDITION;
        }
        return gold;
    }

    /**
     * 获取加成后的金币数
     *
     * @param student
     * @param gold
     * @return
     */
    public static Double getGoldAddition(Student student, Integer gold) {
        if (gold == null) {
            throw new IllegalArgumentException("gold can't be null");
        }
        return getGoldAddition(student, gold * 1.0);
    }

    private StudentGoldAdditionUtil() {
    }

}
