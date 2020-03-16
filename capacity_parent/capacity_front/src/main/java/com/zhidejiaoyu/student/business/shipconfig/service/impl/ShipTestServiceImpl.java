package com.zhidejiaoyu.student.business.shipconfig.service.impl;

import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.mapper.StudentExpansionMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.VocabularyMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleGauntletMapper;
import com.zhidejiaoyu.common.pojo.Gauntlet;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.vo.testVo.beforestudytest.SubjectsVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipAddEquipmentService;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipTestService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class ShipTestServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements ShipTestService {

    @Resource
    private ShipIndexService shipIndexService;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private VocabularyMapper vocabularyMapper;
    @Resource
    private SimpleGauntletMapper simpleGauntletMapper;
    @Resource
    private StudentExpansionMapper studentExpansionMapper;
    @Resource
    private ShipAddEquipmentService shipAddEquipmentService;

    @Override
    public Object getTest(HttpSession session, Long studentId) {
        Student student = getStudent(session);
        Map<String, Object> returnMap = new HashMap<>();
        //查询一小时内的pk次数
        int pkCount = getPkCount(student);
        if (pkCount == 1) {
            returnMap.put("status", 2);
            return returnMap;
        }
        //查询pk信息
        //1.pk发起人的信息
        // 学生装备的飞船及装备信息
        returnMap.put("status", 1);
        returnMap.put("originator", shipIndexService.getStateOfWeek(student.getId()));
        //2.被pk人的信息
        returnMap.put("challenged", shipIndexService.getStateOfWeek(studentId));
        //3.查询题目
        returnMap.put("subject", getSubject(student.getId()));
        return returnMap;
    }

    @Override
    public Object saveTest(HttpSession session, Long beChallenged, Integer type) {
        Student student = getStudent(session);
        StudentExpansion expansion = studentExpansionMapper.selectByStudentId(student.getId());
        Map<String, Object> returnMap = new HashMap<>();
        //查询一小时内的pk次数
        int pkCount = getPkCount(student);
        if (pkCount == 1) {
            returnMap.put("status", 2);
            return returnMap;
        }
        Gauntlet gauntlet = new Gauntlet();
        if (type.equals(1)) {
            gauntlet.setChallengeStatus(1);
            gauntlet.setBeChallengerStatus(2);
            expansion.setStudyPower(expansion.getStudyPower() + 5);
            StudentExpansion beChallengedStudent = studentExpansionMapper.selectByStudentId(beChallenged);
            if (beChallengedStudent.getStudyPower() > expansion.getStudyPower()) {
                expansion.setStudyPower(expansion.getStudyPower() + 5);
                studentExpansionMapper.updateById(expansion);
            }
        } else {
            gauntlet.setChallengeStatus(2);
            gauntlet.setBeChallengerStatus(1);
            expansion.setStudyPower(expansion.getStudyPower() - 5 >= 0 ? expansion.getStudyPower() - 5 : 0);
            studentExpansionMapper.updateById(expansion);
        }

        gauntlet.setChallengerStudentId(student.getId());
        gauntlet.setBeChallengerStudentId(beChallenged);
        gauntlet.setCreateTime(new Date());
        shipAddEquipmentService.updateLeaderBoards(student);
        returnMap.put("status", 1);
        return returnMap;
    }

    private int getPkCount(Student student) {
        Date date = new Date();
        String beforeTime = DateUtil.beforeHoursTime(1);
        int count = simpleGauntletMapper.getCountByStudentIdAndTime(student.getId(), date.toString(), beforeTime);
        if (count >= 5) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * 查询题目信息
     *
     * @param studentId
     * @return
     */
    private Object getSubject(Long studentId) {
        //1，获取单元
        List<Long> unitIds = learnNewMapper.getUnitIdByStudentIdAndType(studentId, 1);
        //2,获取单元题目
        if (unitIds != null && unitIds.size() > 0) {
            List<SubjectsVO> subjectsVos = vocabularyMapper.selectSubjectsVOByUnitIds(unitIds);
            if (subjectsVos.size() > 15) {
                subjectsVos = subjectsVos.subList(0, 15);
            } else {
                List<SubjectsVO> subjectsVos1 = new ArrayList<>();
                subjectsVos1.addAll(subjectsVos);
                while (subjectsVos1.size() < 15) {
                    subjectsVos.forEach(subject -> {
                        if (subjectsVos1.size() < 15) {
                            subjectsVos1.add(subject);
                        }
                    });
                }
                subjectsVos = subjectsVos1;
            }
            return subjectsVos;
        }
        return null;
    }


}
