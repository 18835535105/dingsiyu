package com.zhidejiaoyu.student.business.controller.simple;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.simple.SimpleCourseService;
import com.zhidejiaoyu.common.vo.CoursePlanVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 课程
 *
 * @author qizhentao
 * @version 1.0
 */
@Validated
@Controller
@RequestMapping("/api/course")
public class SimpleCourseController {

    /**
     * 课程业务接口
     */
    @Autowired
    private SimpleCourseService courseService;


    /**
     * 学习页的课程列表 - 精简版
     *	展示的课程和分配的课程和模块相关
     *		默认正在学习的课程state:true
     *
     * @param phase 学段，小学、初中、高中
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSimpleCourse")
    public ServerResponse<Object> getSimpleCourseAll(HttpSession session, String phase){
    	return courseService.getSimpleCourse(session, phase);
    }

    /**
     * 学习页的单元列表 - 精简版
     * 根据课程展示的单元
     * 默认正在学习的单元state:true
     *
     * @param type    1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写
     * @param typeStr 模块名
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSimpleUnit")
    public ServerResponse<Object> getSimpleUnitAll(HttpSession session, long courseId, int type, String typeStr){
    	return courseService.getSimpleUnitAll(session, courseId, type, typeStr);
    }

    /**
     * 生成试卷选择课程 - 精简版
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSimpleTestCourseAll")
    public ServerResponse<Object> getSimpleTestWordCourseAll(HttpSession session){
    	Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
    	return courseService.getSimpleTestWordCourseAll(student.getId());
    }

    /**
     * 点击课程或单元,保存当前模块学习课程单元
     *
     * @param model 9模块对应的数值
     */
    @ResponseBody
    @PostMapping("/postCourseIdAndUnitId")
    public ServerResponse<Object> postCourseIdAndUnitId(HttpSession session, Long courseId, Long unitId, int model){
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
    	return courseService.postCourseIdAndUnitId(student.getId(), courseId, unitId, model);
    }

    /**
     * (学生 版本字段不为空)
     * 点击所有课程, 学生信息绑定的版本下的所有年级 标签  --> 根据选择版本 显示版本下所有单元
     *
     * @return 版本下的所有年级, grade : 年级
     */
    @ResponseBody
    @RequestMapping(value = "/grade")
    public ServerResponse<Object> chooseGrade(HttpSession session) {
        return ServerResponse.createBySuccess(courseService.chooseGrade(session));
    }

    /**
     * (业务员 教材版本字段为空)
     * 1.年级
     *
     * @return 所有年级
     */
    @ResponseBody
    @RequestMapping(value = "/gradegain")
    public ServerResponse<List<Course>> retGrade() {
        List<Course> courses = courseService.retGrade();
        return ServerResponse.createBySuccess(courses);
    }

    /**
     * (业务员 教材版本字段为空)
     * 2.版本
     *
     * @param grade 年级名
     * @return 所有版本
     */
    @ResponseBody
    @RequestMapping(value = "/versiongain")
    public ServerResponse<Object> retVersion(String grade) {
        List<Course> courses = courseService.retVersion(grade);
        return ServerResponse.createBySuccess(courses);
    }

    /**
     * (业务员 教材版本字段为空)
     * 3.标签
     *
     * @param grade   年级名
     * @param version 版本名
     * @return 标签
     */
    @ResponseBody
    @RequestMapping(value = "/labelgain")
    public ServerResponse<Object> retLabel(String grade, String version) {
        List<Course> courses = courseService.retLabel(grade, version);
        return ServerResponse.createBySuccess(courses);
    }

    /**
     * 获取课程进度弹框内容
     *
     * @param session
     * @param courseId
     * @param type 学习进度分类：1：单词类学习进度；2：句子类学习进度
     * @return
     */
    @ResponseBody
    @GetMapping("/getCoursePlan")
    public ServerResponse<List<CoursePlanVo>> getCoursePlan(HttpSession session, Long courseId, @RequestParam(required = false, defaultValue = "1") Integer type) {
        if (courseId == null) {
            return ServerResponse.createByErrorMessage("参数非法");
        }
        return courseService.getCoursePlan(session, courseId, type);
    }

    /**
     * 1.1任务课程 - 精简版
     *
     */
    @ResponseBody
    @RequestMapping(value="/taskCourse")
    public ServerResponse<Object> taskCourse(HttpSession session){
        return courseService.taskCourse(session);
    }

    /**
     * 1.2 任务课程中 - 一键复习  每个模块数量
     */
    @ResponseBody
    @RequestMapping(value="/buildReview")
    public ServerResponse<Object> buildReview(HttpSession session){
        return courseService.buildReview(session);
    }

    /**
     * 所有课程 - 精简版
     *
     * @param studentId 学生id
     */
    @ResponseBody
    @PostMapping(value="/getStudentCourseAll")
    public ServerResponse<Object> getStudentCourseAll(long studentId){
    	return  courseService.getStudentCourseAll(studentId);
    }

    /**
     * 我的课程 - 精简版
     * 	学过的课程
     *
     * @return  course_id 课程id
     * 			course_name 课程名称
     * 			learn_time 最后学习时间
     *
     * @param sort 排序 1=时间从大到小 , 2相反; 默认1
     */
    @ResponseBody
    @RequestMapping(value="/myCourse")
    public ServerResponse<Object> myCourse(HttpSession session, Integer sort){
        if (sort == null) {
            return ServerResponse.createByErrorMessage("sort can't be null!");
        }
    	return  courseService.myCourse(session, sort);
    }

    /**
     * 根据课程id获取所有单元信息 - 精简版
     *
     * @param courseId 课程id
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/allUnit")
    public ServerResponse<Object> allUnit(Integer courseId){
        return courseService.allUnit(courseId);
    }

    /**
     * 获取课程下的所有单元信息及每单元的单词数量
     *
     * @param courseId  课程id
     * @return
     */
    @ResponseBody
    @GetMapping("/getAllUnit")
    public ServerResponse<List<Map<String, Object>>> getAllUnit(Long courseId, @RequestParam(required = false, defaultValue = "true") Boolean showAll) {
        if (courseId == null) {
            throw new RuntimeException("参数错误！");
        }
        return courseService.getAllUnit(courseId, showAll);
    }

    /**
     * 保存选择的课程
     * @Param courseId 课程id
     * @param model 1=单词, 2=例句
     */
    @ResponseBody
    @RequestMapping(value="/postCourse")
    public ServerResponse<Object> postStudentByCourse(@NotNull Integer courseId, @NotNull @Min(1) @Max(2) Integer model, HttpSession session){
    	return courseService.postStudentByCourse(courseId, model, session);
    }


    /**
     * 我的课程
     * 	点击学习
     * @param courseId 课程id
     */
    @ResponseBody
    @RequestMapping(value="/clickLearn")
    public ServerResponse<Object> clickLearn(Integer courseId, int model, HttpSession session){
        Assert.notNull(courseId, "courseId can't be null!");
    	return courseService.clickLearn(courseId, model, session);
    }

    /**
     * 单元测试 - 保存单元到学生信息中
     *
     * @param session
     * @param unitId
     * @param model 单词模块:1  例句模块:2
     * @return
     */
    @ResponseBody
    @PostMapping(value="/postUnit")
    public ServerResponse<Object> postUnit(HttpSession session, int unitId, int model){
        return courseService.postUnit(session, unitId, model);
    }

    /**
     * 获取学生当前模块下的所有课程 id 和 版本
     *
     * @param session
     * @param type
     * @param isAll true（默认）:显示“全部”字段；false：不显示“全部”字段
     * @return
     */
    @ResponseBody
    @GetMapping("/getAllCourse")
    public ServerResponse<List<Map<String, Object>>> getAllCourse(HttpSession session, Integer type,
                                                                  @RequestParam(required = false, defaultValue = "true") Boolean isAll) {
       if (type == null) {
           return ServerResponse.createByErrorMessage("type 不能为空！");
       }
        return courseService.getAllCourse(session, type, isAll);
    }
}
