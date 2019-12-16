package com.zhidejiaoyu.common.utils;

import com.zhidejiaoyu.common.mapper.CcieMapper;
import com.zhidejiaoyu.common.mapper.CourseMapper;
import com.zhidejiaoyu.common.pojo.Ccie;
import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.rank.RankOpt;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 证书保存工具类
 *
 * @author wuchenxi
 * @date 2018/8/27
 */
@Slf4j
@Component
public class CcieUtil {

    @Autowired
    private CcieMapper ccieMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private RankOpt rankOpt;

    /**
     * 测试证书
     * @param student
     * @param testType
     * 			1:单元闯关测试，2:复习测试，3:已学测试，4:生词测试，5:熟词测试，6：五维测试(能力值测试),7:课程前测,8;课程后测,9:单元前测
     * 			10:单元后测,11:能力值测试,12:生句测试,13:熟句测试
     *
     * @param classify
     * @param courseId
     * @param unitId
     */
    public void saveCcieTest(Student student, Integer testType, Integer classify, Long courseId, Long unitId, Integer point) {
        saveCcie(student, 1, testType, classify, courseId, unitId, point);
    }

    /**
     * 保存课程证书
     *
     * @param student
     * @param courseId
     * @param unitId
     */
    public void saveCourseCcie(Student student, Long courseId, Long unitId, Integer point) {
        saveCcie(student, 2, 1, 0, courseId, unitId, point);
    }

    private void saveCcie(Student student, Integer type, Integer testType, Integer classify, Long courseId, Long unitId, Integer point) {
        Course course = courseMapper.selectById(courseId);
        Ccie ccie = new Ccie();
        ccie.setEncourageWord("名列前茅");
        ccie.setTestType(testType);
        ccie.setStudyModel(classify);
        ccie.setGetTime(new Date());
        ccie.setStudentId(student.getId());
        ccie.setStudentName(student.getStudentName());
        ccie.setUnitId(unitId);
        ccie.setPoint(point);
        if (course != null) {
            ccie.setCourseName(course.getCourseName());
        }
        ccie.setReadFlag(0);
        ccie.setCcieNo(getNo(type));
        ccieMapper.insert(ccie);

        rankOpt.optCcieRank(student);
    }

    /**
     * 获取证书编号
     *
     * @param type 1：测试证书；2：课程证书
     * @return  证书编号
     */
    private String getNo(Integer type) {
        StringBuilder sb = new StringBuilder();
        if (type == 1) {
            sb.append("N");
        } else if (type == 2) {
            sb.append("K");
        }
        String today = DateUtil.formatDate(new Date(), "yyyyMMdd");
        sb.append(today);
        if (type == 1) {
            today = "N" + today;
        } else {
            today = "K" + today;
        }
        String maxCcieNo = ccieMapper.selectMaxCcieNo(type, today);
        if (StringUtils.isEmpty(maxCcieNo)) {
            sb.append("1000");
        } else {
            int no = Integer.parseInt(maxCcieNo.substring(maxCcieNo.length() - 4));
            sb.append(no + 1);
        }
        return sb.toString();
    }
}
