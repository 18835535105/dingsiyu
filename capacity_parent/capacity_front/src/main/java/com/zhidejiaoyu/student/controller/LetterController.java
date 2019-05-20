package com.zhidejiaoyu.student.controller;


import com.zhidejiaoyu.student.service.LetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;

/**
 * 字母相关 controller
 *
 * @author zdjy
 * @since 2019-05-17
 */
@Controller
@RequestMapping("/letter")
public class LetterController {

    @Autowired
    private LetterService letterService;
    /**
     * 获取字母单元
     */
    public Object getLetterUnit(HttpSession session){
        return letterService.getLetterUnit(session);
    }




}

