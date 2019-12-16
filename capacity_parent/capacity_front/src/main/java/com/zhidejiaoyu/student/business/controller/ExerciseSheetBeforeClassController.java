package com.zhidejiaoyu.student.business.controller;


import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.ExerciseSheetBeforeClassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 课前测试习题表 前端控制器
 * </p>
 *
 * @author zdjy
 * @since 2019-09-09
 */
@Slf4j
@RestController
@RequestMapping("/exerciseSheetBeforeClass")
public class ExerciseSheetBeforeClassController extends BaseController {

    @Autowired
    private ExerciseSheetBeforeClassService exerciseSheetBeforeClassService;

    /**
     * 获取所有课程信息
     */

    @RequestMapping("/getAllCourse")
    public Object getAllCourse(String studyParagraph) {
        return exerciseSheetBeforeClassService.getAllCourse(studyParagraph);
    }

    /**
     * 根据课程id
     * 获取全部单元信息
     *
     * @param courseId
     * @return
     */
    @RequestMapping("/getUnitByCourseId")
    public Object getUnitByCourseId(Long courseId) {
        return exerciseSheetBeforeClassService.getUnitByCourseId(courseId);
    }

    /**
     * 获取小学单元评测
     *
     * @param session
     * @param jointNames 单元名称
     * @return
     */
    @GetMapping("/small/getBeforeStudyUnitTest")
    public Object getSmallBeforeStudyUnitTest(HttpSession session, String[] jointNames) {
        if (jointNames == null || jointNames.length == 0) {
            Student student = super.getStudent(session);
            log.error("学生[{} -{} - {}]在获取小学学前测试题目时，jointNames 为空！",
                    student.getId(), student.getAccount(), student.getStudentName());
            return ServerResponse.createByErrorMessage("参数错误！");
        }
        return exerciseSheetBeforeClassService.getSmallBeforeStudyUnitTest(session, jointNames);
    }

}

