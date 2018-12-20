package com.zhidejiaoyu.student.controller;

import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.CourseService;
import com.zhidejiaoyu.student.vo.CoursePlanVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 课程
 *
 * @author qizhentao
 * @version 1.0
 */
@Controller
@RequestMapping("/course")
public class CourseController {

    /**
     * breakthrough
     * 课程业务接口
     */
    @Autowired
    private CourseService courseService;

    @ResponseBody
    @GetMapping("/getVersion")
    public ServerResponse<List<Map<String, Object>>> getVersion(HttpSession session) {
        return courseService.getVersion(session);
    }

    @ResponseBody
    @GetMapping("/getCourseByVersion")
    public ServerResponse<List<Map<String, Object>>> getCourseByVersion(HttpSession session, String versionName) {
        return courseService.getCourseByVersion(session, versionName);
    }

    /**
     * 获取学生所有课程id和课程名
     *
     * @param session
     * @return
     */
    @ResponseBody
    @GetMapping("/getAllCourseInfo")
    public ServerResponse<List<Map<String, Object>>> getAllCourse(HttpSession session) {
        return courseService.getAllCoursesInfo(session);
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
     * @param type     学习进度分类：1：单词类学习进度；2：句子类学习进度
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
     * 1.1任务课程 - 展示当前用户六个模块复习量达到10/20的信息和一键复习每个模块的总复习
     * 当某个课程‘智能记忆’模块下的待复习量达到20个时，该课程会出现在‘任务课程’内，让学生抓紧复习。其他模块都是待复习量达到10个即可。
     *
     * @param model 1=单词模块  2=例句模块
     */
    @ResponseBody
    @RequestMapping(value = "/taskCourse")
    public ServerResponse<Object> taskCourse(HttpSession session, Integer model) {
        return courseService.taskCourse(session, model);
    }

    /**
     * 1.2 任务课程中 - 一键复习  每个模块数量
     */
    @ResponseBody
    @RequestMapping(value = "/buildReview")
    public ServerResponse<Object> buildReview(HttpSession session) {
        return courseService.buildReview(session);
    }

    /**
     * 我的课程
     * 学过的课程
     *
     * @param model 1=单词模块 2=例句模块
     * @param sort  排序 1=时间从大到小 , 2相反; 默认1
     * @return course_id 课程id
     * course_name 课程名称
     * learn_time 最后学习时间
     */
    @ResponseBody
    @RequestMapping(value = "/myCourse")
    public ServerResponse<Object> myCourse(HttpSession session, Integer model, Integer sort) {
        if (model == null) {
            return ServerResponse.createByErrorMessage("model can't be null!");
        }
        if (sort == null) {
            return ServerResponse.createByErrorMessage("sort can't be null!");
        }
        return courseService.myCourse(session, model, sort);
    }

    /**
     * 根据课程id获取所有单元信息
     *
     * @param courseId 课程id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/allUnit")
    public ServerResponse<Object> allUnit(Integer courseId) {
        return courseService.allUnit(courseId);
    }

    /**
     * 获取课程下的所有单元信息及每单元的单词数量
     *
     * @param courseId 课程id
     * @param showAll true：含有“全部单元”字样；false:不含有“全部单元字样”
     * @return
     */
    @ResponseBody
    @GetMapping("/getAllUnit")
    public ServerResponse<List<Map<String, Object>>> getAllUnit(Long courseId, @RequestParam(required = false, defaultValue = "true") Boolean showAll) {
        return courseService.getAllUnit(courseId, showAll);
    }

    /**
     * 获取学生可学习的所有课程及课程下单词/例句数量
     *
     * @param session
     * @param type    1:单词；2：句子
     * @return courseName:课程名称 <br>
     * count:当前课程下单词或例句的个数 <br>
     * courseId:课程id
     */
    @ResponseBody
    @GetMapping("/getAllCourses")
    public ServerResponse<List<Map<String, Object>>> getAllCourses(HttpSession session, @RequestParam(required = false, defaultValue = "1") Integer type) {
        return courseService.getAllCourses(session, type);
    }

    /**
     * 保存选择的课程(该接口已废弃）
     *
     * @param model 1=单词, 2=例句
     * @Param courseId 课程id
     */
    @ResponseBody
    @RequestMapping(value = "/postCourse")
    public ServerResponse<Object> postStudentByCourse(Integer courseId, Integer model, HttpSession session) {
        return courseService.postStudentByCourse(courseId, model, session);
    }


    /**
     * 我的课程(该接口已废弃)
     * 点击学习
     *
     * @param courseId 课程id
     */
    @ResponseBody
    @RequestMapping(value = "/clickLearn")
    public ServerResponse<Object> clickLearn(Integer courseId, int model, HttpSession session) {
        Assert.notNull(courseId, "courseId can't be null!");
        return courseService.clickLearn(courseId, model, session);
    }

    /**
     * 点击单元 - 单元闯关展示
     *
     * @param model 单词/例句模块   1=单词 2=例句
     */
    @ResponseBody
    @RequestMapping(value = "/breakthrough")
    public ServerResponse<Object> breakthrough(HttpSession session, Integer model) {
        return courseService.breakthrough(session, model);
    }

    /**
     * 单元测试 - 保存单元到学生信息中
     *
     * @param session
     * @param unitId
     * @param model   单词模块:1  例句模块:2
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/postUnit")
    public ServerResponse<Object> postUnit(HttpSession session, int unitId, int model) {
        return courseService.postUnit(session, unitId, model);
    }

    /**
     * 分页获取当前课程下所有单元信息
     *
     * @param session
     * @param courseId
     * @return
     */
    @ResponseBody
    @GetMapping("/getUnitPage")
    public ServerResponse<PageInfo<Map<String, Object>>> getUnitPage(HttpSession session, Long courseId,
                                                                     @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                                     @RequestParam(required = false, defaultValue = "12") Integer pageSize) {
        if (courseId == null) {
            return ServerResponse.createByErrorMessage("courseId 不能为空！");
        }

        return courseService.getUnitPage(session, courseId, pageNum, pageSize);

    }
}
