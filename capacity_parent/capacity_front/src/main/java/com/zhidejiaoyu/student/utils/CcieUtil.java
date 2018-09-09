package com.zhidejiaoyu.student.utils;

import com.zhidejiaoyu.common.mapper.CcieMapper;
import com.zhidejiaoyu.common.pojo.Ccie;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
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
@Component
public class CcieUtil {

    @Autowired
    private CcieMapper ccieMapper;

    /**
     * 测试证书
     * @param student
     * @param testType   测试类型
     * @param type 1：测试证书；2：课程证书
     */
    public void saveCcieTest(Student student, Integer testType, Integer type) {
        Ccie ccie = new Ccie();
        ccie.setEncourageWord("名列前茅");
        ccie.setGetTime(new Date());
        ccie.setStudentId(student.getId());
        ccie.setStudentName(student.getStudentName());
        ccie.setTestType(testType);
        ccie.setUnitId(student.getUnitId());
        ccie.setReadFlag(0);
        ccie.setCcieNo(getNo(type));
        ccieMapper.insert(ccie);
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
        String maxCcieNo = ccieMapper.selectMaxCcieNo(type, today);
        if (StringUtils.isEmpty(maxCcieNo)) {
            sb.append("1000");
        } else {
            int no = Integer.valueOf(maxCcieNo.substring(maxCcieNo.length() - 4));
            sb.append(no + 1);
        }
        return sb.toString();
    }
}
