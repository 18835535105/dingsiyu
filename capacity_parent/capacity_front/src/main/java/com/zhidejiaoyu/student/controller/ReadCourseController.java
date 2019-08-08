package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.ReadCourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/readCourse")
public class ReadCourseController extends BaseController {

    @Autowired
    private ReadCourseService readService;


    /**
     * 获取全部分配的课程
     *
     * @param session
     * @return
     */
    @RequestMapping("/getAllCourse")
    public ServerResponse<Object> getAllCourse(HttpSession session,String grade) {
        return readService.getAllCourse(session,grade);
    }

    /**
     * 修改正在学习课程信息
     *
     * @param session
     * @param unitId  要修改的课程id
     * @return
     */
    @RequestMapping("/updStudyPlan")
    public ServerResponse<Object> updStudyPlan(HttpSession session, Long unitId, String grade) {
        return readService.updStudyPlan(session, unitId, grade);
    }

    /**
     * 获取正在学习的课程信息
     *
     * @param session
     * @param unitId
     * @return
     */
    @RequestMapping("/getStudyCourse")
    public ServerResponse<Object> getStudyCourse(HttpSession session, Long unitId, String grade) {
        return readService.getStudyCourse(session, unitId, grade);
    }

    /**
     * 获取数据
     * @param typeId        类型id
     * @param courseId      课程id
     * @return
     */
    @RequestMapping("/getContent")
    public ServerResponse<Object> getContent(Long typeId, Long courseId) {
        return readService.getContent(typeId, courseId);
    }

    /**
     * 智能匹配（计算当前阅读的熟词率）
     *
     * @param session
     * @param courseId
     * @param readTypeId
     * @return
     */
    @GetMapping("/capacityMatching")
    public ServerResponse capacityMatching(HttpSession session, Long courseId, Long readTypeId) {
        if (courseId == null || readTypeId == null) {
            log.error("获取阅读智能匹配数据参数错误！courseId=[{}], readTypeId=[{}]", courseId, readTypeId);
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        return readService.capacityMatching(session, courseId, readTypeId);
    }

    @RequestMapping("/getVersion")
    public ServerResponse<Object> getVersion(HttpSession session){
        return readService.getVersion(session);
    }

    /**
     *
     * @param courseId
     * @param type  1,开心一刻   2,迷你英语   3,队长游世界
     * @return
     */
    @GetMapping("getEnglishParadise")
    public ServerResponse<Object> getEnglishParadise(Long courseId,Integer type){
        return readService.getEnglishParadise(courseId,type);
    }
}
