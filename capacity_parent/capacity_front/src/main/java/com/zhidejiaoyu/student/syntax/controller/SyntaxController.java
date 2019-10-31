package com.zhidejiaoyu.student.syntax.controller;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.controller.BaseController;
import com.zhidejiaoyu.student.syntax.service.SyntaxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 超级语法
 *
 * @author: wuchenxi
 * @Date: 2019/10/29 11:34
 */
@Slf4j
@RestController
    @RequestMapping("/syntax")
public class SyntaxController extends BaseController {

    @Resource
    private SyntaxService syntaxService;

    /**
     * 获取学生学习课程
     *
     * @param session
     * @return
     */
    @RequestMapping("/getStudyCourse")
    public Object getStudyCourse(HttpSession session) {
        return syntaxService.getStudyCourse(session);
    }


    /**
     * 获取学生当前单元需要学习的模块名称
     *
     * @param unitId
     * @return
     */
    @GetMapping("/getSyntaxNode")
    public ServerResponse getSyntaxNode(Long unitId) {
        if (unitId == null) {
            Student student = super.getStudent(HttpUtil.getHttpSession());
            log.error("学生[{} - {} - {}]在请求获取语法学习模块的接口时，unitId=null", student.getId(), student.getAccount(), student.getStudentName());
            throw new RuntimeException("参数错误");
        }
        return syntaxService.getSyntaxNode(unitId);
    }


}
