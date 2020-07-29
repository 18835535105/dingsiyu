package com.dfdz.teacher.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.teacher.course.service.CourseService;
import com.dfdz.teacher.redis.FlowConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.grade.GradeUtil;
import com.zhidejiaoyu.common.utils.grade.LabelUtil;
import com.zhidejiaoyu.common.utils.study.PriorityUtil;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseNewMapper, CourseNew> implements CourseService {

    @Resource
    private SchoolTimeMapper schoolTimeMapper;
    @Resource
    private CourseNewMapper courseNewMapper;
    @Resource
    private UnitNewMapper unitNewMapper;
    @Resource
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;

    @Override
    public void deleteStudyUnit(Student student) {
        // 当前月份
        DateTime dateTime = new DateTime();
        int monthOfYear = dateTime.getMonthOfYear();
        // 当前月的第几周
        int weekOfMonth = DateUtil.getWeekOfMonth(dateTime.toDate());

        //获取所有添加单元id
        List<Map<String, Long>> response = this.getStudentPlanResult(student, monthOfYear, weekOfMonth);
        if (response != null) {
            addBasePlanUnit(response, student);
            return;
        }

        response = this.getSchoolPlanResult(student, monthOfYear, weekOfMonth);
        if (response != null) {
            addBasePlanUnit(response, student);
            return;
        }
        response = this.getBasePlanResult(student, monthOfYear, weekOfMonth);
        if (response != null) {
            addBasePlanUnit(response, student);
        }
    }

    /**
     * 获取学校的测试题
     *
     * @param student
     * @param monthOfYear
     * @param weekOfMonth
     * @return
     */
    public List<Map<String, Long>> getSchoolPlanResult(Student student, int monthOfYear, int weekOfMonth) {
        SchoolTime schoolTime;
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);
        List<String> list=new ArrayList<>();
        list.add(student.getGrade());
        if (schoolAdminId != null) {
            // 当前月中小于或等于当前周的最大周数据
            schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek((long) schoolAdminId, 1, monthOfYear, weekOfMonth, list);
            if (schoolTime != null) {
                return this.getSubjectsResult(schoolTime);
            }

            // 小于或等于当前月的最大月、最大周数据
            schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek((long) schoolAdminId, 1, monthOfYear, null, list);
            if (schoolTime != null) {
                return this.getSubjectsResult(schoolTime);
            }

            // 查看学生最大月、最大周数据
            schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek((long) schoolAdminId, 1, null, null,list);
            if (schoolTime != null) {
                return this.getSubjectsResult(schoolTime);
            }
        }
        return null;
    }
    /**
     * 获取总部的测试题
     *
     * @param
     * @return
     */
    public List<Map<String, Long>> getBasePlanResult(Student student, int monthOfYear, int weekOfMonth) {
        SchoolTime schoolTime;
        List<String> list=new ArrayList<>();
        list.add(student.getGrade());
        // 当前月中小于或等于当前周的最大周数据
        schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek(1L, 1, monthOfYear, weekOfMonth, list);
        if (schoolTime != null) {
            return this.getSubjectsResult(schoolTime);
        }

        // 小于或等于当前月的最大月、最大周数据
        schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek(1L, 1, monthOfYear, null, list);
        if (schoolTime != null) {
            return this.getSubjectsResult(schoolTime);
        }

        // 查看学生最大月、最大周数据
        schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek(1L, 1, null, null, list);
        if (schoolTime != null) {
            return this.getSubjectsResult(schoolTime);
        }
        return null;
    }

    /**
     * 为改变年级的学生添加数据
     *
     * @param response
     * @param student
     */
    public void addBasePlanUnit(List<Map<String, Long>> response, Student student) {
        //获取当前学生现有的学习数据
        List<StudentStudyPlanNew> studentStudyPlanNews = studentStudyPlanNewMapper.selectAllByStudentId(student.getId());
        //删除课程集合
        List<StudentStudyPlanNew> deleteList = new ArrayList<>();
        //添加课程集合
        List<Map<String, Long>> addList = new ArrayList<>();
        //获取添加集合
        if (studentStudyPlanNews.size() > 0) {
            for (Map<String, Long> map : response) {
                List<StudentStudyPlanNew> unitId = studentStudyPlanNews.stream()
                        .filter(plan -> (plan.getUnitId().equals(map.get("unitId"))))
                        .collect(Collectors.toList());
                if (unitId.size() == 0) {
                    addList.add(map);
                }
            }
        } else {
            addList = response;
        }
        if (studentStudyPlanNews.size() > 0) {
            studentStudyPlanNews.forEach(plan -> {
                List<Map<String, Long>> unitId = response.stream().
                        filter(map -> map.get("unitId").equals(plan.getUnitId())).collect(Collectors.toList());
                if (unitId.size() == 0) {
                    deleteList.add(plan);
                }
            });
        }
        if (deleteList.size() > 0) {
            List<Integer> deletePlan = new ArrayList<>();
            studentStudyPlanNews.forEach(plan -> deletePlan.add(plan.getId()));
            studentStudyPlanNewMapper.deleteByIds(deletePlan);
        }
        if (addList.size() > 0) {
            List<Long> unitIds = new ArrayList<>();
            addList.forEach(map -> unitIds.add(map.get("unitId")));
            List<Map<String, Object>> maps = unitNewMapper.selectByIds(unitIds);
            maps.forEach(map -> {
                int basePriority = PriorityUtil.getBasePriority(student.getGrade(),
                        map.get("gradeStr") == null || map.get("gradeStr") == "" ? map.get("grade").toString() : map.get("gradeStr").toString(),
                        map.get("labelStr") == null || map.get("labelStr") == "" ? map.get("label").toString() : map.get("labelStr").toString(), 0);
                saveStudentStudyPlanNew(student, map, basePriority, FlowConstant.BEFORE_GROUP_GAME_EASY, 1);
                saveStudentStudyPlanNew(student, map, basePriority - 100, FlowConstant.BEFORE_GROUP_GAME_HARD, 2);
                saveStudentStudyPlanNew(student, map, basePriority - 150, FlowConstant.GOLD_TEST, 3);

            });
        }
    }

    public void saveStudentStudyPlanNew(Student student, Map<String, Object> map, int basePriority, long flowId, int easyOrHard) {
        StudentStudyPlanNew plan = new StudentStudyPlanNew();
        plan.setCourseId(Long.parseLong(map.get("courseId").toString()));
        plan.setStudentId(student.getId());
        plan.setGroup(1);
        plan.setUnitId(Long.parseLong(map.get("unitId").toString()));
        plan.setUpdateTime(new Date());
        plan.setComplete(1);
        plan.setCurrentStudyCount(0);
        plan.setTotalStudyCount(0);
        plan.setErrorLevel(1);
        plan.setEasyOrHard(easyOrHard);
        plan.setFlowId(flowId);
        plan.setBaseLevel(basePriority);
        //判断当前unitId学生的第几个学习单元
        plan.setTimeLevel(PriorityUtil.BASE_TIME_PRIORITY * getUnitIndex(Long.parseLong(map.get("courseId").toString()), Long.parseLong(map.get("unitId").toString())));
        plan.setFinalLevel(plan.getBaseLevel() + plan.getTimeLevel() + plan.getErrorLevel());
        studentStudyPlanNewMapper.insert(plan);
    }

    private int getUnitIndex(long courseId, long unitId) {
        List<Long> courseIds = new ArrayList<>();
        //获取当前课程上下册
        CourseNew courseNew = courseNewMapper.selectById(courseId);
        courseIds.add(courseNew.getId());
        if (courseNew.getLabel().equals("上册") || courseNew.getLabel().equals("下册")) {
            if (courseNew.getLabel().equals("上册")) {
                List<Integer> integers = courseNewMapper.selectCourse(courseNew.getVersion(), courseNew.getGrade(), "下册");
                if (integers != null && integers.size() > 0) {
                    courseIds.add(integers.get(0).longValue());
                }
            }
            if (courseNew.getLabel().equals("下册")) {
                List<Integer> integers = courseNewMapper.selectCourse(courseNew.getVersion(), courseNew.getGrade(), "上册");
                if (integers != null && integers.size() > 0) {
                    courseIds.add(integers.get(0).longValue());
                }
            }
        }
        List<UnitNew> unitNews = unitNewMapper.selectByCourseIds(courseIds);
        for (int i = 0; i < unitNews.size(); i++) {
            if (unitNews.get(i).equals(unitId)) {
                return i + 1;
            }
        }
        return 1;
    }

    /**
     * 获取学生的测试题
     *
     * @param student
     * @param monthOfYear
     * @param weekOfMonth
     * @return
     */
    public List<Map<String, Long>> getStudentPlanResult(Student student, int monthOfYear, int weekOfMonth) {
        // 当前月中小于或等于当前周的最大周数据
        List<String> list=new ArrayList<>();
        list.add(student.getGrade());
        SchoolTime schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek(student.getId(), 2, monthOfYear, weekOfMonth, list);
        if (schoolTime != null) {
            return this.getSubjectsResult(schoolTime);
        }

        // 小于或等于当前月的最大月、最大周数据
        schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek(student.getId(), 2, monthOfYear, null, list);
        if (schoolTime != null) {
            return this.getSubjectsResult(schoolTime);
        }

        // 查看学生最大月、最大周数据
        schoolTime = schoolTimeMapper.selectByUserIdAndTypeAndMonthAndWeek(student.getId(), 2, null, null, list);
        if (schoolTime != null) {
            return this.getSubjectsResult(schoolTime);
        }
        return null;
    }

    /**
     * 获取摸底测试题
     *
     * @param schoolTime
     * @return
     */
    private List<Map<String, Long>> getSubjectsResult(SchoolTime schoolTime) {
        CourseNew courseNew = courseNewMapper.selectById(schoolTime.getCourseId());
        if (courseNew == null) {
            log.debug("未查询到id为"+schoolTime.getCourseId()+"的课程！");
            throw new RuntimeException("未查询到课程！");
        }
        List<String> gradeList = GradeUtil.smallThanCurrent(courseNew.getVersion(), schoolTime.getGrade());

        // 查询小于当前年级的所有单元，等于当前年级小于或等于当前单元的所有单元
        return this.getUnitIds(schoolTime, courseNew, gradeList);
    }

    /**
     * 获取小于当前版本但前年级的所有单元ID和当前版本当前年级小于或等于当前单元的所有单元id
     *
     * @param schoolTime
     * @param courseNew
     * @param gradeList  小于或者等于当前年级的所有年级集合
     * @return
     */
    public List<Map<String, Long>> getUnitIds(SchoolTime schoolTime, CourseNew courseNew, List<String> gradeList) {
        List<Map<String, Long>> unitIds = new ArrayList<>();
        int size = gradeList.size();
        if (size > 1) {
            List<String> smallGradeList = gradeList.subList(0, size - 1);
            // 当前版本中小于当前年级的所有单元id
            unitIds.addAll(unitNewMapper.selectMapByGradeListAndVersionAndGrade(courseNew.getVersion(), smallGradeList));
        }
        // 当前版本中等于当前年级小于或者等于当前单元的所有单元id
        unitIds.addAll(this.getUnitIdsLessThanCurrentUnitId(schoolTime.getCourseId(), schoolTime.getUnitId()));

        return unitIds;
    }

    /**
     * 获取当前课程中小于或者等于当前单元的所有单元id
     *
     * @param courseId
     * @param unitId
     * @return
     */
    private List<Map<String, Long>> getUnitIdsLessThanCurrentUnitId(Long courseId, Long unitId) {
        CourseNew courseNew = courseNewMapper.selectById(courseId);
        String label = courseNew.getLabel();
        List<String> lessLabels = LabelUtil.getLessThanCurrentLabel(label);

        // 说明这个课程只有一个标签
        if (lessLabels.size() == 1 && lessLabels.get(0).equals(label)) {
            return unitNewMapper.selectMapLessOrEqualsCurrentIdByCourseIdAndUnitId(courseId, unitId);
        }

        StringBuilder stringBuilder = new StringBuilder();
        List<String> courseNames = lessLabels.stream().map(lessLabel -> {
            stringBuilder.setLength(0);
            return stringBuilder.append(courseNew.getVersion()).append("(").append(courseNew.getGrade()).append("-").append(lessLabel).append(")").toString();
        }).collect(Collectors.toList());

        List<Map<String, Long>> resultList = unitNewMapper.selectIdsMapByCourseNames(courseNames);
        resultList.addAll(unitNewMapper.selectMapLessOrEqualsCurrentIdByCourseIdAndUnitId(courseId, unitId));
        return resultList;
    }


}
