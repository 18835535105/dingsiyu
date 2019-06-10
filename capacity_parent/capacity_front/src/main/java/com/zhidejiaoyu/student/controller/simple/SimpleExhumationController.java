package com.zhidejiaoyu.student.controller.simple;


import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.ExhumationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 抽取的碎片记录表 前端控制器
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
@Controller
@RequestMapping("/api/exhumation")
public class SimpleExhumationController {

    @Autowired
    private ExhumationService exhumationService;
    /**
     * 合成手套印记碎片
     * @return
     */
    @RequestMapping("/addExhumation")
    @ResponseBody
    public ServerResponse<Object> addExhumation(HttpSession session, Integer name, Integer finalName, String finalImgUrl, Integer number, Integer type){
        return exhumationService.addExhumationAndSyntheticRewardsList(name,finalName,finalImgUrl,number,type,session);
    }
    /**
     *  合成皮肤
     * @param session
     * @param name
     * @param finalName
     * @param finalImgUrl
     * @param number
     * @param type
     * @return
     */
    @RequestMapping("/addSkinExhumation")
    @ResponseBody
    public ServerResponse<Object> addSkinExhumation(HttpSession session, Integer name, Integer finalName, String finalImgUrl, Integer number, Integer type, String imgUrl){
        return exhumationService.addSkinExhumationAndStudentSkin(session,name,finalName,finalImgUrl,number,type,imgUrl);
    }


}

