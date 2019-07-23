package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.mapper.ReadCourseMapper;
import com.zhidejiaoyu.common.mapper.StudentStudyPlanMapper;
import com.zhidejiaoyu.common.pojo.ReadCourse;
import com.zhidejiaoyu.common.pojo.StudentStudyPlan;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.ReadCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class ReadCourseServiceImpl extends BaseServiceImpl<ReadCourseMapper, ReadCourse> implements ReadCourseService {

    @Autowired
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Autowired
    private ReadCourseMapper readCourseMapper;

    /**
     * 获取全部单元信息
     *
     * @param session
     * @return
     */
    @Override
    public ServerResponse<Object> getAllCourse(HttpSession session) {
        Long studentId = getStudentId(session);
        List<StudentStudyPlan> studentStudyPlans = studentStudyPlanMapper.selReadCourseByStudentId(studentId);
        if (studentStudyPlans != null && studentStudyPlans.size() > 0) {
            //去掉重复添加的阅读数据
            List<ReadCourse> readCourse = this.getReadCourse(studentStudyPlans);
        }

        return ServerResponse.createByError(500, "未分配课程");
    }

    //去重数据
    private List<ReadCourse> getReadCourse(List<StudentStudyPlan> plans) {
        //获取全部课程id 去重
        Map<Long, ReadCourse> map = new HashMap<>();
        for (StudentStudyPlan plan : plans) {
            List<ReadCourse> readCourses = readCourseMapper.selCourseByStartUnitAndEndUnit(plan.getStartUnitId(), plan.getEndUnitId());
            for (ReadCourse course : readCourses) {
                map.put(course.getId(), course);
            }
        }
        Set<Long> longs = map.keySet();
        List<ReadCourse> list = new ArrayList<>();
        for (Long courseId : longs) {
            list.add(map.get(courseId));
        }
        return list;
    }
}
