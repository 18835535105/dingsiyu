package com.zhidejiaoyu.student.syntax.service.impl;

import com.zhidejiaoyu.common.Vo.syntax.LearnSyntaxVO;
import com.zhidejiaoyu.common.constant.studycapacity.StudyCapacityTypeConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.common.redis.SyntaxRedisOpt;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.syntax.constant.GradeNameConstant;
import com.zhidejiaoyu.student.syntax.constant.SyntaxModelNameConstant;
import com.zhidejiaoyu.student.syntax.service.SyntaxService;
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

    @Resource
    private StudyCapacityMapper studyCapacityMapper;

    @Resource
    private LearnMapper learnMapper;

    @Resource
    private SyntaxRedisOpt syntaxRedisOpt;

    @Resource
    private KnowledgePointMapper knowledgePointMapper;

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
        List<Map<String, Object>> currentGradeList = new ArrayList<>();
        List<Map<String, Object>> previousGradeList = new ArrayList<>();
        for (Map<String, Object> map : studyList) {
            Map<String, Object> useMap = new HashMap<>(1);
            //添加返回年级及英文年级选项
            String grade = map.get("grade").toString();
            String gradeEnglish = getGradeAndLabelEnglishName(grade);
            String label = map.get("label").toString();
            String labelEnglish = getGradeAndLabelEnglishName(label);
            useMap.put("grade", grade + "(" + label + ")");
            useMap.put("englishGrade", gradeEnglish + "-" + labelEnglish);
            //添加课程id以及单元id名称
            Long courseId = Long.parseLong(map.get("courseId").toString());
            Map<String, Object> studyUnit = longStudentStudySyntaxMap.get(courseId);
            useMap.put("courseId", courseId);
            //判断该单元是否正在学习
            Long unitId;
            if (studyUnit != null) {
                unitId = Long.parseLong(studyUnit.get("unitId").toString());
                useMap.put("model", studyUnit.get("model"));
                useMap.put("battle", 2);
                //计算战斗进度
                useMap.put("combatProgress", getCalculateBattleProgress(courseId, unitId, studyUnit.get("model").toString()));
            } else {
                unitId = Long.parseLong(map.get("startId").toString());
                useMap.put("model", SyntaxModelNameConstant.GAME);
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
        returnMap.put("InGrade", student.getGrade());
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
        int modelSize = unitIds.size() * 3;
        //获取已完成模块在所有模块的位置
        int size = 0;
        for (int i = 0; i < unitIds.size(); i++) {
            if (unitIds.get(i).equals(unitId)) {
                size = i + 1;
            }
        }
        int learningSize = (size - 1) * 3;
        if (SyntaxModelNameConstant.SELECT_SYNTAX.equals(model)) {
            learningSize += 1;
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

    @Override
    public ServerResponse getLearnSyntax(Long unitId) {

        Student student = super.getStudent(HttpUtil.getHttpSession());

        int plan = learnMapper.countLearnedSyntax(student.getId(), unitId, SyntaxModelNameConstant.LEARN_SYNTAX);
        int total = syntaxRedisOpt.getTotalKnowledgePointWithUnitId(unitId);

        // 如果有需要复习的，返回需要复习的数据
        ServerResponse studyCapacity = getNeedView(unitId, student, plan, total);
        if (!Objects.isNull(studyCapacity)) {
            return studyCapacity;
        }

        // 如果有可以学习的新知识点，返回新知识点数据
        ServerResponse knowledgePoint = getNewKnowledgePoint(unitId, student, plan, total);
        if (!Objects.isNull(knowledgePoint)) {
            return knowledgePoint;
        }

        // 获取没有达到黄金记忆点的生知识点
        StudyCapacity nextStudyCapacity = studyCapacityMapper.selectUnKnownByStudentIdAndUnitId(student.getId(), unitId, StudyCapacityTypeConstant.LEARN_SYNTAX);
        ServerResponse serverResponse = this.packageNeedViewLearnSyntax(nextStudyCapacity, plan, total);
        if (!Objects.isNull(serverResponse)) {
            return serverResponse;
        }

        // 说明当前单元学语法模块内容都已掌握，进入选语法模块
        this.packageStudentStudySyntax(unitId, student);

        return ServerResponse.createBySuccess();
    }

    private void packageStudentStudySyntax(Long unitId, Student student) {
        StudentStudySyntax studentStudySyntax = studentStudySyntaxMapper.selectByStudentIdAndUnitId(student.getId(), unitId);
        if (!Objects.isNull(studentStudySyntax)) {
            studentStudySyntax.setModel(SyntaxModelNameConstant.SELECT_SYNTAX);
            studentStudySyntax.setUpdateTime(new Date());
            studentStudySyntaxMapper.updateById(studentStudySyntax);
        } else {
            SyntaxUnit syntaxUnit = syntaxUnitMapper.selectById(unitId);
            studentStudySyntaxMapper.insert(StudentStudySyntax.builder()
                    .updateTime(new Date())
                    .model(SyntaxModelNameConstant.SELECT_SYNTAX)
                    .unitId(unitId)
                    .studentId(student.getId())
                    .courseId(!Objects.isNull(syntaxUnit) ? syntaxUnit.getCourseId() : null)
                    .build());
        }
    }

    /**
     * 获取下一个知识点内容
     *
     * @param unitId
     * @param student
     * @param plan
     * @param total
     * @return
     */
    private ServerResponse getNewKnowledgePoint(Long unitId, Student student, int plan, int total) {
        KnowledgePoint knowledgePoint = knowledgePointMapper.selectNextByUnitId(student.getId(), unitId);
        if (!Objects.isNull(knowledgePoint)) {
            return ServerResponse.createBySuccess(LearnSyntaxVO.builder()
                    .content(knowledgePoint.getContent())
                    .syntaxName(knowledgePoint.getName())
                    .total(total)
                    .plan(Math.min(plan, total))
                    .studyNew(true)
                    .build());
        }
        return null;
    }

    /**
     * 获取需要复习的知识点内容
     *
     * @param unitId
     * @param student
     * @param plan
     * @param total
     * @return
     */
    private ServerResponse getNeedView(Long unitId, Student student, int plan, int total) {
        StudyCapacity studyCapacity = studyCapacityMapper.selectLargerThanGoldTimeWithStudentIdAndUnitId(student.getId(), unitId, StudyCapacityTypeConstant.LEARN_SYNTAX);
        return this.packageNeedViewLearnSyntax(studyCapacity, plan, total);
    }

    private ServerResponse packageNeedViewLearnSyntax(StudyCapacity studyCapacity, int plan, int total) {
        if (!Objects.isNull(studyCapacity)) {
            return ServerResponse.createBySuccess(LearnSyntaxVO.builder()
                    .content(studyCapacity.getWordChinese())
                    .syntaxName(studyCapacity.getWord())
                    .total(total)
                    .plan(Math.min(plan, total))
                    .studyNew(false)
                    .build());
        }
        return null;
    }
}
