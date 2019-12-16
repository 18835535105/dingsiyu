package com.zhidejiaoyu.student.business.controller.simple;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.simple.SimpleIStudentExchangePrizeServiceSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 控制器
 *
 * @author zdjy
 * @Date 2019-02-25 09:47:57
 */
@Controller
@RequestMapping("/api/studentExchangePrize")
public class SimpleStudentExchangePrizeController {


    @Autowired
    private SimpleIStudentExchangePrizeServiceSimple studentExchangePrizeService;


    /**
     * 兑换主页查询
     * @param session
     * @return
     */
    @GetMapping("/getAllList")
    @ResponseBody
    public ServerResponse<Object> getAllList(HttpSession session){
        return studentExchangePrizeService.getAllList(session);
    }


    /**
     * 查询可兑换奖励
     * @param page
     * @param row
     * @param session
     * @param type
     * @return
     */
    @GetMapping("/getList")
    @ResponseBody
    public ServerResponse<Object> getList(int page, int row, HttpSession session, int type){
        return studentExchangePrizeService.getList(page,row,session,type);
    }

    /**
     * 兑奖查询
     * @param page
     * @param row
     * @param session
     * @return
     */
    @GetMapping("getExchangePrize")
    @ResponseBody
    public ServerResponse<Object> getExchangePrize(int page, int row, HttpSession session){
        return studentExchangePrizeService.getExchangePrize(page,row,session);
    }

    /**
     * 添加兑换
     * @param session
     * @param prizeId
     * @return
     */
    @PostMapping("addExchangePrize")
    @ResponseBody
    public ServerResponse<Object> addExchangePrize(HttpSession session, Long prizeId){
        return studentExchangePrizeService.addExchangePrize(session,prizeId);
    }




















}




















