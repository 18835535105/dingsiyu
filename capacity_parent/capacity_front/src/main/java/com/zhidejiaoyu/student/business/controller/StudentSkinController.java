package com.zhidejiaoyu.student.business.controller;


import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.simple.SimpleStudentSkinServiceSimple;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * <p>
 * 学生皮肤表 前端控制器
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
@Controller
@RequestMapping("/studentSkin")
public class StudentSkinController {

    @Resource
    private SimpleStudentSkinServiceSimple simpleStudentSkinServiceSimple;

    /**
     * 获取皮肤和皮肤碎片
     *
     * @param session
     * @return
     */
    @PostMapping("/selSkinAndExhumationByStudent")
    @ResponseBody
    public ServerResponse<Object> selSkinAndExhumationByStudent(HttpSession session) {
        return simpleStudentSkinServiceSimple.selSkinAndExhumation(session);
    }


    /**
     * 获取学生皮肤
     *
     * @param session
     * @return
     */
    @PostMapping("/selSkinByStudent")
    @ResponseBody
    public ServerResponse<Object> selSkinByStudent(HttpSession session) {
        return simpleStudentSkinServiceSimple.selSkin(session);
    }


    /**
     * 使用皮肤 和 卸下皮肤
     *
     * @param session
     * @param skinInteger
     * @param dateInteger
     * @return
     */
    @PostMapping("/updUseSkin")
    @ResponseBody
    public ServerResponse<Object> updUseSkin(HttpSession session, Integer skinInteger, Integer dateInteger, Integer type, String imgUrl) {
        return simpleStudentSkinServiceSimple.useSkin(session, skinInteger, dateInteger, type, imgUrl);
    }

    /**
     * 查询正在使用的皮肤
     *
     * @param session
     * @return
     */
    @PostMapping("/selUseSkin")
    @ResponseBody
    public ServerResponse<Object> selUseSkin(HttpSession session) {
        return simpleStudentSkinServiceSimple.selUseSkinById(session);
    }

    /**
     * 获取学生正在使用的皮肤url
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getUseSkinUrl")
    public ServerResponse<Object> getUseSkinUrl() {
        return simpleStudentSkinServiceSimple.getUseSkinUrl();
    }


    /**
     * 钻石换皮肤
     *
     * @param session
     * @param number
     * @param skinInteger
     * @param imgUrl
     * @return
     */
    @PostMapping("/addStudentSkinByDiamond")
    @ResponseBody
    public ServerResponse<Object> addStudentSkinByDiamond(HttpSession session, int number, int skinInteger, String imgUrl) {
        return simpleStudentSkinServiceSimple.addStudentSkinByDiamond(session, number, skinInteger, imgUrl);
    }
}

