package com.zhidejiaoyu.student.business.controller;

import com.zhidejiaoyu.common.vo.student.StudyLocusVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.StudyLocusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 学习轨迹
 *
 * @author wuchenxi
 * @date 2018/8/27
 */
@RestController
@RequestMapping("/locus")
public class StudyLocusController {

    @Resource
    private StudyLocusService studyLocusService;

    /**
     * 获取学习轨迹首次页面数据
     *
     * @param session
     * @param unitId  单元id 如果该字段为空默认初始化学生第一个课程第一单元的数据；如果该字段不为空，默认初始化当前单元的数据
     * @return
     */
    @GetMapping("/getStudyLocusInfo")
    public ServerResponse<StudyLocusVo> getStudyLocusInfo(HttpSession session, @RequestParam(required = false) Long unitId) {
        return studyLocusService.getStudyLocusInfo(session, unitId);
    }

    /**
     * 分页获取学习轨迹中的勋章图片
     *
     * @param session
     * @param unitId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getMedalPage")
    public ServerResponse<Object> getMedalPage(HttpSession session, Long unitId,
                                               @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                               @RequestParam(required = false, defaultValue = "15") Integer pageSize) {
        if (unitId == null) {
            return ServerResponse.createByErrorMessage("unitId 不能为空！");
        }
        return studyLocusService.getMedalPage(session, unitId, pageNum, pageSize);
    }

    /**
     * 分页获取当前课程下所有的单元信息
     *
     * @param session
     * @param courseId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getUnitPage")
    public ServerResponse<Object> getUnitPage(HttpSession session, Long courseId,
                                              @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                              @RequestParam(required = false, defaultValue = "12") Integer pageSize) {
        if (courseId == null) {
            return ServerResponse.createByErrorMessage("courseId 不能为空！");
        }
        return studyLocusService.getUnitPage(session, courseId, pageNum, pageSize);
    }
}
