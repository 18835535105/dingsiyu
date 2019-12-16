package com.zhidejiaoyu.student.business.controller.simple;


import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.simple.SimpleStudentSkinServiceSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
@RequestMapping("/api/studentSkin")
public class SimpleStudentSkinController {

    @Autowired
    private SimpleStudentSkinServiceSimple studentSkinService;

    /**
     * 获取皮肤和皮肤碎片
     * @param session
     * @return
     */
    @PostMapping("/selSkinAndExhumationByStudent")
    @ResponseBody
    public ServerResponse<Object> selSkinAndExhumationByStudent(HttpSession session){
        return studentSkinService.selSkinAndExhumation(session);
    }


    /**
     * 获取学生皮肤
     * @param session
     * @return
     */
    @PostMapping("/selSkinByStudent")
    @ResponseBody
    public ServerResponse<Object> selSkinByStudent(HttpSession session){
        return studentSkinService.selSkin(session);
    }



    /**
     * 使用皮肤 和 卸下皮肤
     * @param session
     * @param skinInteger
     * @param dateInteger
     * @return
     */
    @PostMapping("/updUseSkin")
    @ResponseBody
    public ServerResponse<Object> updUseSkin(HttpSession session, Integer skinInteger, Integer dateInteger, Integer type, String imgUrl){
        return studentSkinService.useSkin(session,skinInteger,dateInteger,type,imgUrl);
    }

    /**
     * 查询正在使用的皮肤
     * @param session
     * @return
     */
    @PostMapping("/selUseSkin")
    @ResponseBody
    public ServerResponse<Object> selUseSkin(HttpSession session){
        return studentSkinService.selUseSkinById(session);
    }


    /**
     * 钻石换皮肤
     * @param session
     * @param number
     * @param skinInteger
     * @param imgUrl
     * @return
     */
    @PostMapping("/addStudentSkinByDiamond")
    @ResponseBody
    public ServerResponse<Object> addStudentSkinByDiamond(HttpSession session, int number, int skinInteger, String imgUrl){
        return studentSkinService.addStudentSkinByDiamond(session,number,skinInteger,imgUrl);
    }
}

