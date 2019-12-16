package com.zhidejiaoyu.student.business.controller;


import com.zhidejiaoyu.common.vo.studentMessage.StudentMessageListVo;
import com.zhidejiaoyu.common.pojo.StudentMessage;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.StudentMessageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 * 个人中心—》消息中心—》消息通知表 前端控制器
 * </p>
 *
 * @author zdjy
 * @since 2018-12-11
 */
@Controller
@RequestMapping("/studentMessage")
public class StudentMessageController {

    @Autowired
    private StudentMessageService studentMessageService;

    @ResponseBody
    @PostMapping("/saveMessage")
    public ServerResponse<String> saveMessage(HttpSession session, StudentMessage studentMessage) {
        if (StringUtils.isEmpty(studentMessage.getTitle())) {
            return ServerResponse.createByError(403, "留言标题不能为空");
        }
        if (studentMessage.getTitle().length() > 30) {
            return ServerResponse.createByError(403, "留言标题字符长度不能超过30");
        }
        if (StringUtils.isEmpty(studentMessage.getContent())) {
            return ServerResponse.createByError(403, "留言内容不能为空");
        }
        if (studentMessage.getContent().length() > 500) {
            return ServerResponse.createByError(403, "留言内容字符长度不能超过500");
        }
        return studentMessageService.saveStudentMessage(session, studentMessage);
    }

    /**
     * 获取消息通知列表内容
     *
     * @param session
     * @return
     */
    @ResponseBody
    @GetMapping("/getMessageList")
    public ServerResponse<List<StudentMessageListVo>> getMessageList(HttpSession session,
                                                                     @RequestParam(required = false, defaultValue = "7") Integer pageSize,
                                                                     @RequestParam(required = false, defaultValue = "1") Integer pageNum) {
        return studentMessageService. getMessageList(session, pageSize, pageNum);
    }

    /**
     * 删除消息
     *
     * @param session
     * @param messageId
     * @return
     */
    @ResponseBody
    @PostMapping("/deleteMessage")
    public ServerResponse<Object> deleteMessage(HttpSession session, Integer messageId) {
        return studentMessageService.deleteMessage(session, messageId);
    }

    /**
     * 撤销消息
     *
     * @param session
     * @param messageId
     * @return
     */
    @ResponseBody
    @PostMapping("/cancelMessage")
    public ServerResponse<Object> cancelMessage(HttpSession session, Integer messageId) {
        return studentMessageService.cancelMessage(session, messageId);
    }
}

