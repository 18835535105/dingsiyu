package com.zhidejiaoyu.student.syntax.controller;

import com.zhidejiaoyu.student.controller.BaseController;
import com.zhidejiaoyu.student.syntax.service.SyntaxService;
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
@RestController
@RequestMapping("/syntax")
public class SyntaxController extends BaseController {

    @Resource
    private SyntaxService syntaxService;

    /**
     * 获取学生学习课程
     * @param session
     * @return
     */
    @RequestMapping("/getStudyCourse")
    public Object getStudyCourse(HttpSession session){
        return syntaxService.getStudyCourse(session);
    }




}
