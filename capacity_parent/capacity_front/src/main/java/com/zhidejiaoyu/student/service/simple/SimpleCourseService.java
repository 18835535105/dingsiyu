package com.zhidejiaoyu.student.service.simple;

import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.student.vo.CoursePlanVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 课程业务接口层
 *
 * @author qizhentao
 * @version 1.0
 */
public interface SimpleCourseService extends SimpleBaseService<Course> {

    /**
     * 点击所有课程 查询学生绑定版本下的所有年级 标签
     *
     * @return 版本下的所有年级
     */
    List chooseGrade(HttpSession session);

    List<Course> retGrade();

    List<Course> retVersion(String grade);

    List<Course> retLabel(String grade, String version);

    /**
     * 获取课程进度弹框内容
     *
     * @param session
     * @param courseId
     * @param type     学习进度分类：1：单词类学习进度；2：句子类学习进度
     * @return
     */
    ServerResponse<List<CoursePlanVo>> getCoursePlan(HttpSession session, Long courseId, Integer type);

    ServerResponse<Object> taskCourse(HttpSession session);

    ServerResponse<Object> buildReview(HttpSession session);

    /**
     * 我的课程接口
     *
     * @param session
     * @param model
     * @return
     */
    ServerResponse<Object> myCourse(HttpSession session, Integer sort);

    ServerResponse<Object> allUnit(Integer courseId);

    ServerResponse<Object> postStudentByCourse(Integer unitId, Integer model, HttpSession session);

	ServerResponse<Object> clickLearn(Integer courseId, int model, HttpSession session);

    /**
     * 获取课程下的所有单元信息及每单元的单词数量
     *
     * @param courseId  课程id
     * @param showAll
     * @return
     */
    ServerResponse<List<Map<String,Object>>> getAllUnit(Long courseId, Boolean showAll);

    ServerResponse<Object> postUnit(HttpSession session, int unitId, int model);

	ServerResponse<Object> getSimpleCourseAll(HttpSession session, String typeStr, int type);

	ServerResponse<Object> getSimpleUnitAll(HttpSession session, long courseId, int type, String typeStr);

	ServerResponse<Object> getSimpleTestWordCourseAll(long studentId);

	ServerResponse<Object> getStudentCourseAll(long studentId);

    /**
     * 获取当前学生 当前模块下的所有课程 id 和 版本
     * @param session
     * @param type
     * @param isAll
     * @return
     */
    ServerResponse<List<Map<String, Object>>> getAllCourse(HttpSession session, Integer type, Boolean isAll);

	ServerResponse<Object> postCourseIdAndUnitId(Long studentId, Long courseId, Long unitId, int model);
}
