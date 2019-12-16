package com.zhidejiaoyu.student.business.syntax.service.impl;

import com.zhidejiaoyu.common.vo.syntax.SyntaxCourseVo;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.common.mapper.StudentStudyPlanMapper;
import com.zhidejiaoyu.common.mapper.StudentStudySyntaxMapper;
import com.zhidejiaoyu.common.mapper.SyntaxTopicMapper;
import com.zhidejiaoyu.common.mapper.SyntaxUnitMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentStudySyntax;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.common.pojo.SyntaxUnit;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.syntax.constant.GradeNameConstant;
import com.zhidejiaoyu.student.business.syntax.service.SyntaxService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 语法实现类
 *
 * @author liumaoyu
 * @date 2019-10-30
 */
@Service
public class SyntaxServiceImpl extends BaseServiceImpl<SyntaxTopicMapper, SyntaxTopic> implements SyntaxService {

    @Resource
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Resource
    private StudentStudySyntaxMapper studentStudySyntaxMapper;

    @Resource
    private SyntaxUnitMapper syntaxUnitMapper;

    /**
     * 获取学生学习课程
     *
     * @param session
     * @return
     */
    @Override
    public Object getStudyCourse(HttpSession session) {
        Student student = getStudent(session);
        //获取所有语法课程数据
        List<Map<String, Object>> studyList = studentStudyPlanMapper.selectSyntaxByStudentAndType(student.getId());
        //获取学生所有语法课程学习记录
        Map<Long, Map<String, Object>> longStudentStudySyntaxMap = studentStudySyntaxMapper.selectStudyAllByStudentId(student.getId());
        Map<String, Object> returnMap = new HashMap<>(3);
        List<SyntaxCourseVo> currentGradeList = new ArrayList<>();
        List<SyntaxCourseVo> previousGradeList = new ArrayList<>();
        for (Map<String, Object> map : studyList) {
            SyntaxCourseVo useMap = new SyntaxCourseVo();
            //添加返回年级及英文年级选项
            String grade = map.get("grade").toString();
            String gradeEnglish = getGradeAndLabelEnglishName(grade);
            String label = map.get("label").toString();
            String labelEnglish = getGradeAndLabelEnglishName(label);
            useMap.setGrade(grade + "(" + label + ")");
            useMap.setEnglishGrade(gradeEnglish + "-" + labelEnglish);
            //添加课程id以及单元id名称
            Long courseId = Long.parseLong(map.get("courseId").toString());
            Map<String, Object> studyUnit = longStudentStudySyntaxMap.get(courseId);
            useMap.setCourseId(courseId.intValue());
            //判断该单元是否正在学习
            Long unitId;
            //战斗状态 为2单元已完成
            int complete = Integer.parseInt(map.get("complete").toString());
            if (studyUnit != null) {
                unitId = Long.parseLong(studyUnit.get("unitId").toString());
                useMap.setModel(studyUnit.get("model").toString());
                getBattleAndCombatProgress(studyUnit, complete, useMap, unitId, courseId);
            } else {
                unitId = Long.parseLong(map.get("startId").toString());
                useMap.setModel(null);
                getBattleAndCombatProgress(null, complete, useMap, unitId, courseId);
            }
            SyntaxUnit syntaxUnit = syntaxUnitMapper.selectById(unitId);
            useMap.setUnitId(unitId);
            useMap.setUnitName(syntaxUnit.getUnitName());
            useMap.setUnitIndex(syntaxUnit.getUnitIndex());
            if (grade.equals(student.getGrade())) {
                previousGradeList.add(useMap);
            } else {
                currentGradeList.add(useMap);
            }
        }
        returnMap.put("currentGrade", currentGradeList);
        returnMap.put("previousGrade", previousGradeList);
        returnMap.put("InGrade", student.getGrade());
        return returnMap;
    }

    /**
     * 获取战斗进度和战斗状态
     *
     * @param studyUnit 单元
     * @param complete  1，未完成 2，已完成
     * @param useMap    SyntaxCourseVo返回对象
     * @param unitId    单元id
     * @param courseId  课程id
     */
    private void getBattleAndCombatProgress(Map<String, Object> studyUnit, int complete,
                                            SyntaxCourseVo useMap, Long unitId, Long courseId) {
        useMap.setBattle(complete == 2 ? 3 : (studyUnit == null ? 1 : 2));
        //计算战斗进度
        useMap.setCombatProgress(complete == 2 ? 100 :
                (studyUnit == null ? 0 : getCalculateBattleProgress(courseId, unitId, studyUnit.get("model").toString())));
    }

    /**
     * 计算战斗进度
     *
     * @param courseId
     * @param unitId
     * @param model
     */
    private int getCalculateBattleProgress(Long courseId, Long unitId, String model) {
        //获取所有单元id
        List<Long> unitIds = syntaxUnitMapper.selectUnitIdByCourseId(courseId);
        //获取总模块数量
        int modelSize = unitIds.size() * 3;
        //获取已完成模块在所有模块的位置
        int size = 0;
        for (int i = 0; i < unitIds.size(); i++) {
            if (unitIds.get(i) <= unitId) {
                size = i + 1;
            }
        }
        int learningSize = (size - 1) * 3;
        if (SyntaxModelNameConstant.SELECT_SYNTAX.equals(model)) {
            learningSize += 1;
        } else if (SyntaxModelNameConstant.GAME.equals(model)) {
            learningSize += 2;
        } else if (SyntaxModelNameConstant.WRITE_SYNTAX.equals(model)) {
            learningSize += 2;
        }
        return (int) (learningSize * 1.0 / modelSize * 100);
    }


    private String getGradeAndLabelEnglishName(String grade) {
        if (grade == null) {
            return "one";
        }
        if (GradeNameConstant.FIRST_GRADE.equals(grade)) {
            return "one";
        }
        if (GradeNameConstant.SECOND_GRADE.equals(grade)) {
            return "two";
        }
        if (GradeNameConstant.WRITE_GRADE.equals(grade)) {
            return "three";
        }
        if (GradeNameConstant.FOURTH_GRADE.equals(grade)) {
            return "four";
        }
        if (GradeNameConstant.FIFTH_GRADE.equals(grade)) {
            return "five";
        }
        if (GradeNameConstant.SIXTH_GRADE.equals(grade)) {
            return "six";
        }
        if (GradeNameConstant.SEVENTH_GRADE.equals(grade)) {
            return "serven";
        }
        if (GradeNameConstant.EIGHTH_GRADE.equals(grade)) {
            return "eight";
        }
        if (GradeNameConstant.NINTH_GRADE.equals(grade)) {
            return "nine";
        }

        if (GradeNameConstant.SENIOR_ONE.equals(grade)) {
            return "ten";
        }
        if (GradeNameConstant.SENIOR_TWO.equals(grade)) {
            return "eleven";
        }
        if (GradeNameConstant.SENIOR_THREE.equals(grade)) {
            return "twelve";
        }
        if (GradeNameConstant.VOLUME_1.equals(grade)) {
            return "up";
        }
        if (GradeNameConstant.VOLUME_2.equals(grade)) {
            return "down";
        }
        return null;
    }

    @Override
    public ServerResponse getSyntaxNode(Long unitId) {

        Student student = super.getStudent(HttpUtil.getHttpSession());

        StudentStudySyntax studentStudySyntax = studentStudySyntaxMapper.selectByStudentIdAndUnitId(student.getId(), unitId);
        if (Objects.isNull(studentStudySyntaxMapper)) {
            return ServerResponse.createBySuccessMessage(SyntaxModelNameConstant.GAME);
        }

        return ServerResponse.createBySuccessMessage(studentStudySyntax.getModel());
    }

}
