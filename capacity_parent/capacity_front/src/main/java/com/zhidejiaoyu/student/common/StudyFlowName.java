package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.common.mapper.DurationMapper;
import com.zhidejiaoyu.common.mapper.LearnMapper;
import com.zhidejiaoyu.common.mapper.StudentWorkDayMapper;
import com.zhidejiaoyu.common.pojo.StudentWorkDay;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 一级,二级标签计算规则
 */
@Component
public class StudyFlowName {

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private StudentWorkDayMapper studentWorkDayMapper;

    @Autowired
    private DurationMapper durationMapper;

    /**
     * 已学单词数
     */
    private int countWord;
    /**
     * 已学例句数
     */
    private int countExample;
    /**
     * 已掌握单词数
     */
    private int graspWord;
    /**
     * 已掌握例句数
     */
    private int graspExample;

    /**
     * 获取流程名称
     *
     * @param studentId 学生id
     * @return 流程名称, 学生属于(甲乙丙丁)
     */
    public Map getFlowName(Long studentId) {
        Map<String, String> map = new HashMap();
        // 记录走哪个流程 1=甲 2=乙 3=丙 4=丁
        int record;

        // 已学单词数量
        countWord = learnMapper.labelWordsQuantityByStudentId(studentId);
        // 已学例句数量
        countExample = learnMapper.labelExamplesQuantityByStudentId(studentId);
        // 已掌握单词
        graspWord = learnMapper.labelGraspWordsByStudentId(studentId);
        // 已掌握例句
        graspExample = learnMapper.labelGraspExamplesByStudentId(studentId);
        // 平均分
        int point = learnMapper.testAverageScoreByStudentId(studentId);
        int word = countWord == 0 ? 0 : graspWord / countWord * 100;
        int example = countExample == 0 ? 0 : graspExample / countExample * 100;

        if(word >= 90 || example >= 90 || point >= 90){
            record = 1;
        }else if(word >= 80 || example >= 80 || point >= 80){
            record = 2;
        }else if(word >= 30 || example >= 40 ||  point >= 30){
            record = 3;
        }else{
            record = 4;
        }
        String flowName = getFlow(studentId, record);
        map.put("flowName", flowName == null ? "" : flowName);
        map.put("type", record+"");
        return map;
    }

    /**
     * 获取学生应该学习的流程
     *
     * @param studentId
     * @param firstLabel
     * @return
     */
    private String getFlow(Long studentId, int firstLabel) {
        switch (firstLabel) {
            case 1:
            case 2:
                return jiaYiFlow(studentId, firstLabel);
            case 3:
            case 4:
                return bingDingFlow(studentId, firstLabel);
            default:
        }
        return null;
    }

    /**
     * 获取丙丁级流程
     *
     * @param studentId
     * @param firstLabel
     * @return
     */
    private String bingDingFlow(Long studentId, int firstLabel) {
        // 是否含有苦学无效
        boolean hardStudy = (countWord * 1.0 / graspWord <= 0.7) || (countExample * 1.0 / graspExample <= 0.6);

        // 是否含有贪玩厌学
        boolean hitStudy = hitStudy(studentId);
        int flag = 0;
        if (hardStudy && hitStudy) {
            flag = 3;
        } else if (hardStudy) {
            flag = 1;
        } else if (hitStudy) {
            flag = 2;
        }

        if (firstLabel == 3) {
            if (flag == 0) {
                return "流程9";
            }
            if (flag == 1) {
                return "流程10";
            }
            if (flag == 2) {
                return "流程11";
            }
            return "流程12";
        }

        if (firstLabel == 4) {
            if (flag == 0) {
                return "流程13";
            }
            if (flag == 1) {
                return "流程14";
            }
            if (flag == 2) {
                return "流程15";
            }
            return "流程16";
        }
        return null;
    }

    /**
     * 判断学生是否贪玩厌学
     * <br>
     * 每登录平台学习7个工作日，当有效学习时间/在线时长=学习效率<=60%，则该学生被贴上“贪玩厌学”的标签。
     *
     * @param studentId
     * @return
     */
    private boolean hitStudy(Long studentId) {
        StudentWorkDay studentWorkDay = studentWorkDayMapper.selectPreviousWorkDay(studentId);
        if (studentWorkDay == null) {
            // 说明当前学生从第一次登陆系统到当前时间还不足7个工作日
            return false;
        }

        // 获取学生7个工作日的学习效率
        Double studyEfficiency = durationMapper.selectStudyEfficiency(studentWorkDay);
        if (studyEfficiency == null) {
            // 如果学生7个工作日内没有登陆系统，不符合标签 贪玩厌学
            return false;
        }
        return studyEfficiency <= 0.6;
    }

    /**
     * 获取甲级、乙级学生流程
     *
     * @param studentId
     * @param firstLabel
     * @return 0:暂无 1：粗心大意 2：眼高手低 3：粗心大意、眼高手低
     */
    private String jiaYiFlow(Long studentId, int firstLabel) {
        // 是否是粗心大意
        boolean careless = careless(studentId);
        // 是否是眼高手低
        boolean eyesLow = eyesLow(studentId);

        int flag = 0;
        if (careless && eyesLow) {
            flag = 3;
        } else if (careless) {
            flag = 1;
        } else if (eyesLow) {
            flag = 2;
        }

        // 甲级标签对应的流程
        if (firstLabel == 1) {
            if (flag == 0) {
                return "流程1";
            }
            if (flag == 1) {
                return "流程2";
            }
            if (flag == 2) {
                return "流程3";
            }
            return "流程4";
        }

        // 乙级标签对应的流程
        if (firstLabel == 2) {
            if (flag == 0) {
                return "流程5";
            }
            if (flag == 1) {
                return "流程6";
            }
            if (flag == 2) {
                return "流程7";
            }
            return "流程8";
        }
        return "";
    }

    /**
     * 判断学生是否含有“眼高手低”标签
     *
     * @param studentId
     * @return
     */
    private boolean eyesLow(Long studentId) {
        // 完成单元效率(完成单元效率=（已完成单元数÷总单元数[学生能学习的总单元数，不区分课程]）/有效学习总次数)
        Double completeUnitRate = learnMapper.selectCompleteUnitRate(studentId);
        // 测试卷错误率（学测试卷所有错误题数除以所有已学测试卷题目数为错误率）
        double testErrorRate = learnMapper.selectTestErrorRate(studentId);
        return completeUnitRate >= 0.4 && testErrorRate >= 0.2;
    }

    /**
     * 判断学生是否含有“粗心大意”标签
     *
     * @param studentId
     * @return 失误率 >= 30% return <code>true</code> 二级标签含有粗心大意
     * otherwise return <code>false</code>
     */
    private boolean careless(Long studentId) {
        // 首次学习为熟词/熟句状态的 单词和例句总个数
        int firstIsKnownCount = learnMapper.countTotalLearnCountWithFirstIsKnown(studentId);
        // 出现失误的单词和例句总个数
        int faultWordCount = learnMapper.countFaultWordByStudentId(studentId);
        // 失误率
        double faultRate = BigDecimalUtil.div(faultWordCount * 1.0, firstIsKnownCount, 2);
        return faultRate >= 0.3;
    }

}
