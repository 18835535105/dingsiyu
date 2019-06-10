package com.zhidejiaoyu.student.utils.simple;

import com.zhidejiaoyu.common.mapper.simple.CcieMapper;
import com.zhidejiaoyu.common.pojo.Ccie;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.simple.dateUtlis.DateUtil;
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
     * @param studyModel
     * 			7: 五维测试(能力值测试); 11:单词辨音; 12:词组辨音; 13:快速单词; 14:快速词组; 15:词汇考点; 16:快速句型; 17:语法辨析;
     * 			18单词默写; 19:词组默写;'
     * @param point 测试得分
     */
    public void saveCcieTest(Student student, Integer testType, Integer type, Integer studyModel, Integer point) {
        Ccie ccie = new Ccie();
        ccie.setEncourageWord("名列前茅");
        ccie.setStudyModel(studyModel);
        ccie.setGetTime(new Date());
        ccie.setStudentId(student.getId());
        ccie.setStudentName(student.getStudentName());
        ccie.setTestType(testType);
        ccie.setUnitId(student.getUnitId());
        ccie.setCourseName(student.getCourseName());
        ccie.setReadFlag(0);
        ccie.setCcieNo(getNo(type));
        ccie.setPoint(point);
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

    /**
     * 组装学习模块和测试类型
     *
     * @param model
     * @param testName
     * @return
     */
    public static String getModelAndTestType(Integer model, Object testName) {
        if (model == -1 && testName != null) {
            return testName.toString();
        }
        if (model <= 7) {
            String[] modelArr = {"单词图鉴", "慧记忆", "慧听写", "慧默写", "例句听力", "例句翻译", "例句默写", "五维测试"};
            return modelArr[model] + testName;
        }
        if (model >= 11 && model <= 19) {
            String[] modelArr = {"单词辨音", "词组辨音", "快速单词", "快速词组", "词汇考点", "快速句型", "语法辨析", "单词默写", "词组默写"};
            return modelArr[model - 11] + testName;
        }
        return "";
    }

    /**
     * 匹配证书名称
     *
     * @param testType
     * @param studyModel
     * @return
     */
    public static String getCcieName(Integer testType, Integer studyModel) {
        if (testType != null && studyModel != null) {
            if (studyModel >= 11 && studyModel <= 19) {
                // 清学版学习模块
                if (testType == 9) {
                    // 单元前测
                    return "自由之塔-挑战者";
                }
                if (testType == 1) {
                    // 单元闯关测试
                    return "自由之塔-开拓者";
                }
                if (testType == 8) {
                    // 学后测试
                    return "自由之塔-征服者";
                }
            } else if (studyModel == -1) {
                if (testType == 10) {
                    // 能力值测试
                    return "全能战士";
                }
                if (testType == 6) {
                    // 五维测试
                    return "学富五车";
                }
            } else {
                // 同步版学习模块
                if (studyModel == 1 && testType == 1) {
                    // 慧记忆单元闯关测试
                    return "最强大脑";
                }
                if (studyModel == 0 && testType == 1) {
                    // 单词图鉴单元闯关测试
                    return "火眼金睛";
                }
                if (studyModel == 2 && testType == 1) {
                    // 慧听写单元闯关测试
                    return "耳听八方";
                }
                if (studyModel == 3 && testType == 1) {
                    // 慧默写单元闯关测试
                    return "过目不忘";
                }
                if (testType == 3) {
                    // 已学测试
                    return "学而时习";
                }
                if (testType == 4) {
                    // 生词测试
                    return "勇攀高峰";
                }
                if (testType == 5) {
                    // 熟词测试
                    return "温故知新";
                }
            }
        }
        return "";
    }
}
