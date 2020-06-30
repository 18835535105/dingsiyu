package com.zhidejiaoyu.student.business.wechat.smallapp.controller;

import com.github.pagehelper.util.StringUtil;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.StudentInfoService;
import com.zhidejiaoyu.student.business.wechat.smallapp.serivce.FlyOfStudyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 智慧飞行记录
 *
 * @author: wuchenxi
 * @date: 2020/6/2 15:20:20
 */
@RestController
@RequestMapping("/smallApp/fly")
public class FlyOfStudyController {

    @Resource
    private FlyOfStudyService flyOfStudyService;

    @Resource
    private StudentInfoService studentInfoService;

    /**
     * 通过扫码获取学生学习信息
     *
     * @param openId
     * @param num    二维码序号 <ul>
     *               <li>-1：查询学生总的学习情况信息</li>
     *               <li>其他：查询指定序号的学习信息</li>
     *               </ul>
     * @return
     */
    @GetMapping("/getStudyInfo")
    public ServerResponse<Object> getStudyInfo(String openId, Integer num) {
        checkParam(openId, num);
        if (num == -1) {
            return flyOfStudyService.getTotalStudyInfo(openId);
        }
        return flyOfStudyService.getStudyInfo(openId, num);
    }

    /**
     * 通过扫码获取学生学习信息
     *
     * @param studentUuid
     * @param num         二维码序号 <ul>
     *                    <li>-1：查询学生总的学习情况信息</li>
     *                    <li>其他：查询指定序号的学习信息</li>
     *                    </ul>
     * @return
     */
    @GetMapping("/v1/getStudyInfo")
    public ServerResponse<Object> getStudyInfoByUuid(@RequestParam String studentUuid, @RequestParam Integer num) {
        Student student = studentInfoService.getByUuid(studentUuid);
        return this.getStudyInfo(student.getOpenid(), num);
    }


    private void checkParam(String openId, Integer num) {
        if (StringUtil.isEmpty(openId)) {
            throw new ServiceException("openId can't be null!");
        }
        if (num == null) {
            throw new ServiceException("num can't be null!");
        }
    }

    /**
     * 获取学生指定二维码对应的照片信息
     *
     * @param openId
     * @param num    查询指定序号对应的日期拍摄的照片
     * @return
     */
    @GetMapping("/getStudentInfo")
    public ServerResponse<Object> getStudentInfo(String openId, Integer num) {
        checkParam(openId, num);
        return flyOfStudyService.getStudentInfo(openId, num);
    }

    @GetMapping("/v1/getStudentInfo")
    public ServerResponse<Object> getStudentInfoV1(@RequestParam String studentUuid, @RequestParam Integer num) {
        Student student = studentInfoService.getByUuid(studentUuid);
        return this.getStudentInfo(student.getOpenid(), num);
    }

}
