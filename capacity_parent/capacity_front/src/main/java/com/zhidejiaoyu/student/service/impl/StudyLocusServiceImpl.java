package com.zhidejiaoyu.student.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.Vo.student.CcieVo;
import com.zhidejiaoyu.common.Vo.student.StudyLocusVo;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.Ccie;
import com.zhidejiaoyu.common.pojo.RunLog;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.constant.LevelConstant;
import com.zhidejiaoyu.student.service.CourseService;
import com.zhidejiaoyu.student.service.StudyLocusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wuchenxi
 * @date 2018/8/27
 */
@Service
public class StudyLocusServiceImpl implements StudyLocusService {


    @Autowired
    private CourseService courseService;

    @Autowired
    private CcieMapper ccieMapper;

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Autowired
    private UnitSentenceMapper unitSentenceMapper;

    @Override
    public ServerResponse<StudyLocusVo> getStudyLocusInfo(HttpSession session, Long unitId) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        StudyLocusVo studyLocusVo = new StudyLocusVo();

        if (unitId == null) {
            // 获取当前学生的所有课程信息
            List<Map<String, Object>> courseList = getCourseInfo(session, studyLocusVo);

            // 获取当前学生当前课程的所有单元信息
            PageInfo<Map<String, Object>> unitPageData = getUnitsInfo(session, studyLocusVo, courseList);

            unitId = Long.valueOf(unitPageData.getList().get(0).get("id").toString());
        }

        // 获取学生当前单元最新获取的证书信息
        getCcieInfo(student, studyLocusVo, unitId);

        // 获取学生当前单元最新获取的勋章信息
        getMedalInfo(student, studyLocusVo, unitId);

        // 本单元获取的金币数
        getUnitGoldInfo(student, studyLocusVo, unitId);

        // 本单元通关等级
        getLevel(student, studyLocusVo, unitId);

        // 战胜率（本单元所有闯关成功的次数/所有闯关次数）
        Double rate = testRecordMapper.selectVictoryRate(student.getId(), unitId);
        if (rate != null) {
            studyLocusVo.setVictoryRate(String.valueOf(rate * 100));
        }

        return ServerResponse.createBySuccess(studyLocusVo);
    }

    @Override
    public ServerResponse<Object> getMedalPage(HttpSession session, Long unitId, Integer pageNum, Integer pageSize) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<String> medalUrlPageInfo = getMedalUrlPageInfo(student, unitId);
        return ServerResponse.createBySuccess(medalUrlPageInfo);
    }

    @Override
    public ServerResponse<Object> getUnitPage(HttpSession session, Long courseId, Integer pageNum, Integer pageSize) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        PageInfo<Map<String, Object>> unitPageData = getUnitPageInfo(session, student, courseId, pageNum, pageSize);
        return ServerResponse.createBySuccess(unitPageData);
    }

    private void getLevel(Student student, StudyLocusVo studyLocusVo, Long unitId) {
        // 已掌握单词
        int graspWord = learnMapper.labelGraspWordsByStudentId(student.getId());
        // 已掌握例句
        int graspExample = learnMapper.labelGraspExamplesByStudentId(student.getId());
        int totalWord = unitVocabularyMapper.allCountWord(unitId);
        int totalSentence = unitSentenceMapper.countByUnitId(unitId);
        if (totalSentence + totalWord != 0) {
            List<String> level = new ArrayList<>(3);
            double knownRate = (graspWord + graspExample) * 1.0 / (totalSentence + totalWord);
            if (knownRate >= 0.8 && knownRate <= 100) {
                level.add(LevelConstant.ONE_SUCCESS);
                level.add(LevelConstant.TWO_FAILED);
                level.add(LevelConstant.THREE_FAILED);
            } else if (knownRate < 0.8 && knownRate >= 0.5) {
                level.add(LevelConstant.ONE_FAILED);
                level.add(LevelConstant.TWO_SUCCESS);
                level.add(LevelConstant.THREE_FAILED);
            } else {
                level.add(LevelConstant.ONE_FAILED);
                level.add(LevelConstant.TWO_FAILED);
                level.add(LevelConstant.THREE_SUCCESS);
            }
            studyLocusVo.setLevelList(level);
        }
    }

    private void getUnitGoldInfo(Student student, StudyLocusVo studyLocusVo, Long unitId) {
        List<RunLog> goldRunLogs = runLogMapper.selectGoldByUnitId(student.getId(), unitId, 4);
        int gold = 0;
        for (RunLog goldRunLog : goldRunLogs) {
            gold += Integer.valueOf(goldRunLog.getLogContent().split("#")[1]);
        }
        studyLocusVo.setGoldCount(gold);
    }

    private void getMedalInfo(Student student, StudyLocusVo studyLocusVo, Long unitId) {
        PageHelper.startPage(1, 15);
        PageInfo<String> urlPageInfo = getMedalUrlPageInfo(student, unitId);
        studyLocusVo.setMedalUrlPageInfo(urlPageInfo);
    }

    private PageInfo<String> getMedalUrlPageInfo(Student student, Long unitId) {
        List<RunLog> runLogs = runLogMapper.selectGoldByUnitId(student.getId(), unitId, 7);
        List<String> medalUrl = new ArrayList<>(runLogs.size());
        for (RunLog runLog : runLogs) {
            String[] url = runLog.getLogContent().split("#");
            if (url.length == 1) {
                medalUrl.add(url[0]);
            } else {
                medalUrl.add(url[1]);
            }
        }
        return new PageInfo<>(medalUrl);
    }

    private void getCcieInfo(Student student, StudyLocusVo studyLocusVo, Long unitId) {
        PageHelper.startPage(1, 1);
        List<Ccie> ccie = ccieMapper.selectLastCcie(student, unitId);
        List<CcieVo> ccieVos = new ArrayList<>(ccie.size());
        CcieVo ccieVo;
        for (Ccie ccie1 : ccie) {
            ccieVo = new CcieVo(ccie1);
            ccieVos.add(ccieVo);
        }
        PageInfo<Ccie> cciePageInfo = new PageInfo<>(ccie);
        PageInfo<CcieVo> ccieVoPageInfo = new PageInfo<>(ccieVos);
        ccieVoPageInfo.setTotal(cciePageInfo.getTotal());
        ccieVoPageInfo.setPages(cciePageInfo.getPages());
        studyLocusVo.setCciePageInfo(ccieVoPageInfo);
    }

    private PageInfo<Map<String, Object>> getUnitsInfo(HttpSession session, StudyLocusVo studyLocusVo, List<Map<String, Object>> courseList) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long courseId = (Long) courseList.get(0).get("courseId");
        PageInfo<Map<String, Object>> unitPageData = getUnitPageInfo(session, student, courseId, 1, 12);

        studyLocusVo.setUnitPageInfo(unitPageData);
        return unitPageData;
    }

    private PageInfo<Map<String, Object>> getUnitPageInfo(HttpSession session, Student student, Long courseId, int pageNum, int pageSize) {
        ServerResponse<PageInfo<Map<String, Object>>> unitPage = courseService.getUnitPage(session, courseId, pageNum, pageSize);
        PageInfo<Map<String, Object>> unitPageData = unitPage.getData();

        // 为单元添加已学、未学标识
        addLearnedFlag(student, unitPageData);
        return unitPageData;
    }

    private void addLearnedFlag(Student student, PageInfo<Map<String, Object>> unitPageData) {
        List<Map<String, Object>> unitList = unitPageData.getList();
        List<Long> unitIds = learnMapper.selectLearnedUnitsByStudentId(student.getId());

        if (unitIds.size() == 0) {
            // 一个单元都没有学习
            for (Map<String, Object> stringStringMap : unitList) {
                stringStringMap.put("learned", false);
            }
        } else {
            // 学习了若干单元
            for (Map<String, Object> stringStringMap : unitList) {
                for (Long unitId : unitIds) {
                    if (Objects.equals(unitId, Long.valueOf(stringStringMap.get("id").toString()))) {
                        stringStringMap.put("learned", true);
                        break;
                    } else {
                        stringStringMap.put("learned", false);
                    }
                }
            }
        }
    }

    private List<Map<String, Object>> getCourseInfo(HttpSession session, StudyLocusVo studyLocusVo) {
        ServerResponse<List<Map<String, Object>>> allCourses = courseService.getAllCourses(session, 2);
        List<Map<String, Object>> courseList = allCourses.getData();
        courseList.remove(0);
        for (Map<String, Object> stringObjectMap : courseList) {
            stringObjectMap.remove("count");
        }
        studyLocusVo.setCourseList(courseList);
        return courseList;
    }
}
