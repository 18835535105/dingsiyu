package com.zhidejiaoyu.student.service.simple.impl;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.study.simple.SimpleCommonMethod;
import com.zhidejiaoyu.common.utils.simple.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.SimpleGrowReportService;
import com.zhidejiaoyu.student.vo.reportvo.LearnResultVO;
import com.zhidejiaoyu.student.vo.reportvo.LearnSuperviseVO;
import com.zhidejiaoyu.student.vo.reportvo.ReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * 成长报告相关
 *
 * @author wuchenxi
 * @date 2018/7/19
 */
@Service
public class SimpleGrowReportServiceImpl implements SimpleGrowReportService {

    @Autowired
    private SimpleCommonMethod simpleCommonMethod;

    @Autowired
    private SimpleUnitMapper unitMapper;

    @Autowired
    private SimpleUnitVocabularyMapper simpleUnitVocabularyMapper;

    @Autowired
    private SimpleLearnMapper learnMapper;

    @Autowired
    private SimpleUnitSentenceMapper simpleUnitSentenceMapper;

    @Autowired
    private SimpleDurationMapper simpleDurationMapper;

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Override
    public ServerResponse<ReportVO> getLearnResult(HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        String phase = simpleCommonMethod.getPhase(student.getGrade());
        ReportVO reportVO = new ReportVO();

        // 学习成果
        packageLearnResultVO(student, phase, reportVO);
        // 学习监督
        packageLearnSuperviseVO(student, phase, reportVO);

        return ServerResponse.createBySuccess(reportVO);
    }

    /**
     * 封装学习监督相关数据
     *
     * @param student
     * @param phase
     * @param reportVO
     */
    private void packageLearnSuperviseVO(Student student, String phase, ReportVO reportVO) {
        LearnSuperviseVO learnSuperviseVO = new LearnSuperviseVO();

        // 单词学习总数
        int wordCount = learnMapper.countByStudentId(student.getId(), 1);
        learnSuperviseVO.setWordCount(wordCount);

        // 例句学习总个数
        int sentenceCount = learnMapper.countByStudentId(student.getId(), 2);
        learnSuperviseVO.setSentenceCount(sentenceCount);

        // todo：已学课文，已学口语

        // 已学知识点
        int learnedCount = wordCount + sentenceCount;
        learnSuperviseVO.setLearnedCount(learnedCount);

        // 已学知识点大于learnedCount的学生个数
        int studentCount = learnMapper.countGreaterLearnedCount(learnedCount, phase, student);
        int studentTotalCount = simpleStudentMapper.countByPhaseAndVersion(phase, student);
        learnSuperviseVO.setDefeatRate((int) (BigDecimalUtil.div((studentTotalCount - studentCount) * 1.0, studentTotalCount, 2) * 100));

        // 在线总时长
        Integer totalOnlineTime = simpleDurationMapper.selectTotalOnlineByStudentId(student.getId());
        learnSuperviseVO.setTotalOnlineTime((int) BigDecimalUtil.div(totalOnlineTime * 1.0, 3600, 0));

        // 学习总时长
        Long totalValidTime = simpleDurationMapper.selectValidTimeByStudentId(student.getId());
        learnSuperviseVO.setTotalValidTime((int) BigDecimalUtil.div(totalValidTime * 1.0, 3600, 0));

        // 学习效率
        double efficiency = BigDecimalUtil.div(totalValidTime, totalOnlineTime, 2) * 100;
        learnSuperviseVO.setEfficiency((int) efficiency);
        reportVO.setLearnSupervise(learnSuperviseVO);
    }

    /**
     * 封装学习成果数据
     *
     * @param student
     * @param phase
     * @param reportVO
     */
    private void packageLearnResultVO(Student student, String phase, ReportVO reportVO) {
        String[] types = {"智慧单词", "抢分句型", "原汁原文", "口语跟读"};
        List<LearnResultVO> learnResultVOS = new ArrayList<>(types.length);
        // 封装学习成果
        packageResult(learnResultVOS, student, phase, types);
        reportVO.setLearnResult(learnResultVOS);
    }

    /**
     * 封装学习成果
     * <ul>
     *     <li>掌握单词</li>
     *     <li>应掌握</li>
     *     <li>掌握率</li>
     * </ul>
     * @param learnResultVOS
     * @param student
     * @param phase 学段
     * @param types 学习成果类型
     */
    private void packageResult(List<LearnResultVO> learnResultVOS, Student student, String phase, String[] types) {

        // 查询学生当前学段下所有单元
        List<Long> unitIds = unitMapper.selectIdByPhase(student, phase);
        if (unitIds.size() == 0) {
            LearnResultVO learnResultVO;
            for (String type : types) {
                learnResultVO = new LearnResultVO();
                learnResultVO.setMasterRate(0);
                learnResultVO.setShouldMaster(0);
                learnResultVO.setMaster(0);
                learnResultVO.setType(type);
                learnResultVOS.add(learnResultVO);
            }
        } else {
            LearnResultVO learnResultVO;
            for (int i = 0; i < types.length; i++) {
                learnResultVO = new LearnResultVO();
                learnResultVO.setType(types[i]);
                switch (i) {
                    case 0:
                        // 封装单词相关学习成果
                        packageWordResultVo(student, phase, unitIds, learnResultVO);
                        break;
                    case 1:
                        // 封装例句相关学习成果
                        packageSentenceResultVo(student, phase, unitIds, learnResultVO);
                        break;
                    case 2:
                        // todo:封装课文相关学习成果
                        break;
                    case 3:
                        // todo:封装口语句型相关学习成果
                        break;
                    default:
                }
                learnResultVOS.add(learnResultVO);
            }
        }
    }

    /**
     * 封装例句相关学习成果
     *
     * @param student
     * @param phase
     * @param unitIds
     * @param learnResultVO
     */
    private void packageSentenceResultVo(Student student, String phase, List<Long> unitIds, LearnResultVO learnResultVO) {
        // 查询学生当前学段下所有应学习的句型个数
        int shouldMaster = simpleUnitSentenceMapper.countSentenceByUnitIds(unitIds);
        // 当前学段掌握的例句个数
        int masterCount = learnMapper.countMasterSentence(student, phase);
        packageResult(learnResultVO, shouldMaster, masterCount);
    }

    /**
     * 封装单词相关学习成果
     *
     * @param student
     * @param phase
     * @param unitIds
     * @param learnResultVO
     */
    private void packageWordResultVo(Student student, String phase, List<Long> unitIds, LearnResultVO learnResultVO) {
        // 查询学生当前学段下所有应学习的单词个数
        Long shouldMaster = simpleUnitVocabularyMapper.countWordByUnitIds(unitIds);
        // 当前学段掌握的单词个数
        int masterCount = learnMapper.countMasterWord(student, phase);
        packageResult(learnResultVO, Integer.parseInt(shouldMaster.toString()), masterCount);
    }

    private void packageResult(LearnResultVO learnResultVO, int shouldMaster, int masterCount) {
        double masterRateDouble = BigDecimalUtil.div(masterCount * 1.0, shouldMaster, 2);
        int masterRate = (int) masterRateDouble * 100;
        learnResultVO.setMasterRate(masterRate);
        learnResultVO.setShouldMaster(shouldMaster);
        learnResultVO.setMaster(masterCount);
    }
}
