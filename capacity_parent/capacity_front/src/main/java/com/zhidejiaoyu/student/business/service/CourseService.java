package com.zhidejiaoyu.student.business.service;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.CoursePlanVo;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 课程业务接口层
 *
 * @author qizhentao
 * @version 1.0
 */
public interface CourseService extends BaseService<Course> {

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

    ServerResponse<Object> taskCourse(HttpSession session, Integer model);

    ServerResponse<Object> buildReview(HttpSession session);

    /**
     * 我的课程接口
     *
     * @param session
     * @param model
     * @return
     */
    ServerResponse<Object> myCourse(HttpSession session, Integer model, Integer sort);

    ServerResponse<Object> allUnit(Integer courseId);

    ServerResponse<Object> postStudentByCourse(Integer unitId, Integer model, HttpSession session);

    ServerResponse<Object> clickLearn(Integer courseId, int model, HttpSession session);

    ServerResponse<Object> breakthrough(HttpSession session, Integer model);

    /**
     * 获取课程下的所有单元信息及每单元的单词数量
     *
     *
     * @param session
     * @param courseId 课程id
     * @param showAll   true：含有“全部单元”字样；false:不含有“全部单元字样”
     * @param type
     * @return
     */
    ServerResponse<List<Map<String, Object>>> getAllUnit(HttpSession session, Long courseId, Boolean showAll, Integer type);

    ServerResponse<Object> postUnit(HttpSession session, int unitId, int model);

    /**
     * 获取学生可学习的所有课程及课程下单词/例句数量
     *
     * @param session
     * @param type    1:单词；2：句子
     * @param flag
     * @return
     */
    ServerResponse<List<Map<String, Object>>> getAllCourses(HttpSession session, Integer type, Boolean flag);

    /**
     * 分页获取当前课程下所有单元信息
     *
     * @param session
     * @param courseId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ServerResponse<PageInfo<Map<String, Object>>> getUnitPage(HttpSession session, Long courseId, Integer pageNum, Integer pageSize);

    /**
     * 获取学生所有课程id及课程名称
     *
     * @param session
     * @return
     */
    ServerResponse<List<Map<String, Object>>> getAllCoursesInfo(HttpSession session);

    /**
     * 获取学生所有可学习的版本名称
     *
     * @param session
     * @return
     */
    ServerResponse<List<Map<String, Object>>> getVersion(HttpSession session);

    /**
     * 获取版本下所有课程信息
     *
     * @param session
     * @param versionName
     * @return
     */
    ServerResponse<List<Map<String, Object>>> getCourseByVersion(HttpSession session, String versionName);
}
