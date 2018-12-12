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
     * @param testType
     * 			1:单元闯关测试，2:复习测试，3:已学测试，4:生词测试，5:熟词测试，6：五维测试(能力值测试),7:课程前测,8;课程后测,9:单元前测
     * 			10:单元后测,11:能力值测试
     * @param type
     * 			1：测试证书；2：课程证书
     * @param classify
     * 			0 : 单词图鉴 ; 1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写；7: 五维测试(词汇量测试);
     */
    public void saveCcieTest(Student student, Integer testType, Integer type, Integer classify) {
        Ccie ccie = new Ccie();
        ccie.setEncourageWord("名列前茅");
        ccie.setStudyModel(classify);
        ccie.setGetTime(new Date());
        ccie.setStudentId(student.getId());
        ccie.setStudentName(student.getStudentName());
        ccie.setTestType(testType);
        ccie.setUnitId(student.getUnitId());
        ccie.setUnitId(student.getCourseId());
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
