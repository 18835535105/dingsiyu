package com.zhidejiaoyu.student.syntax.service.impl;

import com.zhidejiaoyu.common.mapper.StudentStudyPlanMapper;
import com.zhidejiaoyu.common.mapper.StudentStudySyntaxMapper;
import com.zhidejiaoyu.common.mapper.SyntaxTopicMapper;
import com.zhidejiaoyu.common.mapper.SyntaxUnitMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentStudySyntax;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.common.pojo.SyntaxUnit;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.syntax.constant.SyntaxModelNameConstant;
import com.zhidejiaoyu.student.syntax.service.SyntaxService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

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
        Map<String, Object> returnMap = new HashMap<>();
        List<Map<String, Object>> currentGradeList = new ArrayList<>();
        List<Map<String, Object>> previousGradeList = new ArrayList<>();
        for (Map<String, Object> map : studyList) {
            Map<String, Object> useMap = new HashMap<>();
            //添加返回年级及英文年级选项
            String grade = map.get("grade").toString();
            String gradeEnglish = getGradeAndLabelEnglishName(grade);
            String label = map.get("label").toString();
            String labelEnglish = getGradeAndLabelEnglishName(label);
            useMap.put("grade", grade + "(" + label + ")");
            useMap.put("engilshGrade", gradeEnglish + "-" + labelEnglish);
            //添加课程id以及单元id名称
            Long courseId = Long.parseLong(map.get("courseId").toString());
            Map<String, Object> studyUnit = longStudentStudySyntaxMap.get(courseId);
            useMap.put("courseId", courseId);
            //判断该单元是否正在学习
            Long unitId = null;
            if (studyUnit != null) {
                unitId = Long.parseLong(studyUnit.get("unitId").toString());
                useMap.put("model", studyUnit.get("model"));
                useMap.put("battle", 2);
                //计算战斗进度
                useMap.put("combatProgress", getCalculateBattleProgress(courseId, unitId, studyUnit.get("model").toString()));
            } else {
                unitId = Long.parseLong(map.get("startId").toString());
                useMap.put("model", null);
                useMap.put("battle", 1);
                useMap.put("combatProgress", 0);
            }
            SyntaxUnit syntaxUnit = syntaxUnitMapper.selectById(unitId);
            useMap.put("unitId", unitId);
            useMap.put("unitName", syntaxUnit.getUnitName());
            //战斗状态
            int complete = Integer.parseInt(map.get("complete").toString());
            if (complete == 2) {
                useMap.put("battle", 3);
                useMap.put("combatProgress", 100);
            }
            if (grade.equals(student.getGrade())) {
                previousGradeList.add(useMap);
            } else {
                currentGradeList.add(useMap);
            }
        }
        returnMap.put("currentGrade", currentGradeList);
        returnMap.put("previousGrade", previousGradeList);
        return returnMap;
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
        Integer modelSize = unitIds.size() * 3;
        //获取已完成模块在所有模块的位置
        Integer size = 0;
        for (int i = 0; i < unitIds.size(); i++) {
            if (unitIds.get(i).equals(unitId)) {
                size = i + 1;
            }
        }
        int learningSize = (size - 1) * 3;
        if (model.equals("选语法")) {
            learningSize += 1;
        } else if (model.equals("写语法")) {
            learningSize += 2;
        }
        return learningSize / modelSize;
    }


    private String getGradeAndLabelEnglishName(String grade) {
        if (grade == null) {
            return "one";
        }
        if (grade.equals("一年级")) {
            return "one";
        }
        if (grade.equals("二年级")) {
            return "two";
        }
        if (grade.equals("三年级")) {
            return "three";
        }
        if (grade.equals("四年级")) {
            return "four";
        }
        if (grade.equals("五年级")) {
            return "five";
        }
        if (grade.equals("六年级")) {
            return "six";
        }
        if (grade.equals("七年级")) {
            return "serven";
        }
        if (grade.equals("八年级")) {
            return "eight";
        }
        if (grade.equals("九年级")) {
            return "nine";
        }

        if (grade.equals("高一")) {
            return "ten";
        }
        if (grade.equals("高二")) {
            return "eleven";
        }
        if (grade.equals("高三")) {
            return "twelve";
        }
        if (grade.equals("上册")) {
            return "up";
        }
        if (grade.equals("下册")) {
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
