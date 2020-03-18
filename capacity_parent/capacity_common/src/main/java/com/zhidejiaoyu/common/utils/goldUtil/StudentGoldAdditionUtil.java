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

    public static Double getGoldAddition(Student student, Double gold) {
        Date date = new Date();
        if (student.getBonusExpires() != null) {
            if (student.getBonusExpires().getTime() >= date.getTime()) {
                return gold * GLOVESADDITION;
            } else {
                return gold;
            }
        }
        return gold;
    }


}
