package com.zhidejiaoyu.student.common.validTime;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.LearnMapper;
import com.zhidejiaoyu.common.mapper.RunLogMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleLearnMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleRunLogMapper;
import com.zhidejiaoyu.common.pojo.LearnExample;
import com.zhidejiaoyu.common.pojo.RunLog;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.study.simple.SimpleCommonMethod;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 获取保存有效时长的提示语
 *
 * @author wuchenxi
 * @date 2019-07-15
 */
@Slf4j
@Component
public class GetValidTimeTip {

    @Autowired
    private SimpleCommonMethod simpleCommonMethod;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private SimpleRunLogMapper simpleRunLogMapper;

    @Autowired
    private SimpleLearnMapper simpleLearnMapper;

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private RunLogMapper runLogMapper;

    /**
     * 计算学生本次学习获得的金币数
     *
     * @param session
     * @param classify
     * @param second    本次学习的有效时长，从进入学习页到退出学习页的时长
     * @param loginTime
     * @return
     */
    public String saveSimpleGoldAward(HttpSession session, Student student, Integer classify, Long second, Date loginTime) {
        String learnType = simpleCommonMethod.getTestType(classify);
        // 金币数
        double gold = 0;
        // 提示语
        if(student.getBonusExpires()!=null){
            if(student.getBonusExpires().getTime() > System.currentTimeMillis()){
                gold *= 1.2;
            }
        }
        StringBuilder tip = new StringBuilder("本次学习获得金币：");
        gold += saveSimpleNewLearnAward(classify, loginTime, learnType, student);
        gold += saveSimpleValidLearnAward(loginTime, learnType, student, second, classify);
        gold += saveSimpleKnownLearnAward(classify, loginTime, learnType, student);
        if (gold > 0) {
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
            studentMapper.updateByPrimaryKeySelective(student);
            student= studentMapper.selectById(student.getId());

            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        }
        tip.append(Math.round(gold)).append(" 个");
        return tip.toString();
    }

    /**
     * 本次学习熟词达到指定数量奖励金币
     *
     * @param classify
     * @param loginTime
     * @param learnType
     * @param student
     * @return
     */
    private int saveSimpleKnownLearnAward(Integer classify, Date loginTime, String learnType, Student student) {
        int learnCount;
        int condition = classify == 1 ? 20 : 10;
        long stuId = student.getId();
        StringBuilder sb = new StringBuilder();
        int awardCount = simpleRunLogMapper.countAwardCount(stuId, DateUtil.formatYYYYMMDDHHMMSS(loginTime), simpleCommonMethod.getTestType(classify), "熟词");
        LearnExample learnExample = new LearnExample();
        learnExample.createCriteria().andStudentIdEqualTo(stuId).andLearnTimeGreaterThanOrEqualTo(loginTime)
                .andStudyModelEqualTo(learnType)
                .andStudyCountEqualTo(1).andStatusEqualTo(1);
        learnCount = simpleLearnMapper.countByExample(learnExample);
        if (learnCount >= condition * (awardCount + 1)) {
            sb.append("学生").append(student.getStudentName()).append("在").append(learnType)
                    .append("模块本次新学熟词大于等于").append(condition).append("个单词，获得#1#个金币，登录时间：").append(DateUtil.formatYYYYMMDDHHMMSS(loginTime));
            RunLog runLog = new RunLog(stuId, 4, sb.toString(), new Date());
            simpleRunLogMapper.insert(runLog);
            log.info(sb.toString());
            return 1;
        }
        return 0;
    }

    /**
     * 本次登录学习有效时长达到30分钟后奖励金币
     *
     * @param loginTime
     * @param learnType
     * @param student
     * @param second
     * @param classify
     * @return
     */
    private int saveSimpleValidLearnAward(Date loginTime, String learnType, Student student, Long second, Integer classify) {
        int condition = 30;
        long minute = second / 60;
        long stuId = student.getId();
        StringBuilder sb = new StringBuilder();
        // 查询本次登录期间当前奖励次数
        int count = simpleRunLogMapper.countAwardCount(stuId, DateUtil.formatYYYYMMDDHHMMSS(loginTime), simpleCommonMethod.getTestType(classify), "有效时长大于等于30分钟");
        if (count == 0 && minute >= condition) {
            sb.append("学生").append(student.getStudentName()).append("在").append(learnType).
                    append("模块学习过程中有效时长大于等于30分钟，获得#5#个金币，登录时间：").append(DateUtil.formatYYYYMMDDHHMMSS(loginTime));
            RunLog runLog = new RunLog(stuId, 4, sb.toString(), new Date());
            simpleRunLogMapper.insert(runLog);
            log.info(sb.toString());
            return 5;
        }
        return 0;
    }

    /**
     * 新学若干单词后奖励金币，本次登录期间只有学习指定模块单词的整数倍才奖励相应金币
     *
     * @param classify
     * @param loginTime
     * @param learnType 学习模块
     * @param student
     * @return 金币个数
     */
    private int saveSimpleNewLearnAward(Integer classify, Date loginTime, String learnType, Student student) {
        int awardCount;
        int learnCount;
        int condition = classify == 1 ? 40 : 20;
        long stuId = student.getId();
        StringBuilder sb = new StringBuilder();
        awardCount = simpleRunLogMapper.countAwardCount(stuId, DateUtil.formatYYYYMMDDHHMMSS(loginTime), simpleCommonMethod.getTestType(classify), "新学");
        LearnExample learnExample = new LearnExample();
        learnExample.createCriteria().andStudentIdEqualTo(stuId).andLearnTimeGreaterThanOrEqualTo(loginTime)
                .andStudyModelEqualTo(learnType)
                .andStudyCountEqualTo(1);
        learnCount = simpleLearnMapper.countByExample(learnExample);
        if (learnCount >= condition * (awardCount + 1)) {
            sb.append("学生").append(student.getStudentName()).append("在").append(learnType).append("模块本次登录新学大于等于")
                    .append(condition).append("个单词，获得#1#个金币，登录时间：").append(DateUtil.formatYYYYMMDDHHMMSS(loginTime));
            RunLog runLog = new RunLog(stuId, 4, sb.toString(), new Date());
            simpleRunLogMapper.insert(runLog);
            log.info(sb.toString());
            return 1;
        }
        return 0;
    }

    /**
     * 计算学生本次学习获得的金币数
     *
     * @param session
     * @param classify
     * @param second    本次学习的有效时长，从进入学习页到退出学习页的时长
     * @param loginTime
     * @return
     */
    public String saveGoldAward(HttpSession session, Student student, Integer classify, Long second, Date loginTime) {
        String learnType = commonMethod.getTestType(classify);
        // 金币数
        double gold = 0;
        if (student.getBonusExpires() != null) {
            if (student.getBonusExpires().getTime() > System.currentTimeMillis()) {
                gold *= 1.2;
            }
        }
        // 提示语
        StringBuilder tip = new StringBuilder("本次学习获得金币：");
        gold += saveNewLearnAward(classify, loginTime, learnType, student);
        gold += saveValidLearnAward(loginTime, learnType, student, second, classify);
        gold += saveKnownLearnAward(classify, loginTime, learnType, student);
        if (gold > 0) {
            student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
            studentMapper.updateByPrimaryKeySelective(student);
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
        }
        tip.append(Math.round(gold)).append(" 个");
        return tip.toString();
    }

    /**
     * 本次学习熟词达到指定数量奖励金币
     *
     * @param classify
     * @param loginTime
     * @param learnType
     * @param student
     * @return
     */
    private int saveKnownLearnAward(Integer classify, Date loginTime, String learnType, Student student) {
        int learnCount;
        int condition = classify == 1 ? 20 : 10;
        long stuId = student.getId();
        StringBuilder sb = new StringBuilder();
        int awardCount = runLogMapper.countAwardCount(stuId, DateUtil.formatYYYYMMDDHHMMSS(loginTime), classify, "熟词");
        LearnExample learnExample = new LearnExample();
        learnExample.createCriteria().andStudentIdEqualTo(stuId).andLearnTimeGreaterThanOrEqualTo(loginTime)
                .andStudyModelEqualTo(learnType)
                .andStudyCountEqualTo(1).andStatusEqualTo(1);
        learnCount = learnMapper.countByExample(learnExample);
        if (learnCount >= condition * (awardCount + 1)) {
            sb.append("学生").append(student.getStudentName()).append("在").append(learnType)
                    .append("模块本次新学熟词大于等于").append(condition).append("个单词，获得#1#个金币，登录时间：").append(DateUtil.formatYYYYMMDDHHMMSS(loginTime));
            RunLog runLog = new RunLog(stuId, 4, sb.toString(), new Date());
            runLog.setUnitId(student.getUnitId());
            runLog.setCourseId(student.getCourseId());
            runLogMapper.insert(runLog);
            log.info(sb.toString());
            return 1;
        }
        return 0;
    }

    /**
     * 本次登录学习有效时长达到30分钟后奖励金币
     *
     * @param loginTime
     * @param learnType
     * @param student
     * @param second
     * @param classify
     * @return
     */
    private int saveValidLearnAward(Date loginTime, String learnType, Student student, Long second, Integer classify) {
        int condition = 30;
        long minute = second / 60;
        long stuId = student.getId();
        StringBuilder sb = new StringBuilder();
        // 查询本次登录期间当前奖励次数
        int count = runLogMapper.countAwardCount(stuId, DateUtil.formatYYYYMMDDHHMMSS(loginTime), classify, "有效时长大于等于30分钟");
        if (count == 0 && minute >= condition) {
            sb.append("学生").append(student.getStudentName()).append("在").append(learnType).
                    append("模块学习过程中有效时长大于等于30分钟，获得#5#个金币，登录时间：").append(DateUtil.formatYYYYMMDDHHMMSS(loginTime));
            RunLog runLog = new RunLog(stuId, 4, sb.toString(), new Date());
            runLog.setUnitId(student.getUnitId());
            runLog.setCourseId(student.getCourseId());
            runLogMapper.insert(runLog);

            log.info(sb.toString());
            return 5;
        }
        return 0;
    }

    /**
     * 新学若干单词后奖励金币，本次登录期间只有学习指定模块单词的整数倍才奖励相应金币
     *
     * @param classify
     * @param loginTime
     * @param learnType 学习模块
     * @param student
     * @return 金币个数
     */
    private int saveNewLearnAward(Integer classify, Date loginTime, String learnType, Student student) {
        int awardCount;
        int learnCount;
        int condition = classify == 1 ?  40 : 20;
        long stuId = student.getId();
        StringBuilder sb = new StringBuilder();
        awardCount = runLogMapper.countAwardCount(stuId, DateUtil.formatYYYYMMDDHHMMSS(loginTime), classify, "新学");
        LearnExample learnExample = new LearnExample();
        learnExample.createCriteria().andStudentIdEqualTo(stuId).andLearnTimeGreaterThanOrEqualTo(loginTime)
                .andStudyModelEqualTo(learnType)
                .andStudyCountEqualTo(1);
        learnCount = learnMapper.countByExample(learnExample);
        if (learnCount >= condition * (awardCount + 1)) {
            sb.append("学生").append(student.getStudentName()).append("在").append(learnType).append("模块本次登录新学大于等于")
                    .append(condition).append("个单词，获得#1#个金币，登录时间：").append(DateUtil.formatYYYYMMDDHHMMSS(loginTime));
            RunLog runLog = new RunLog(stuId, 4, sb.toString(), new Date());
            runLog.setUnitId(student.getUnitId());
            runLog.setCourseId(student.getCourseId());
            runLogMapper.insert(runLog);
            log.info(sb.toString());
            return 1;
        }
        return 0;
    }
}
