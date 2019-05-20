package com.zhidejiaoyu.student.controller;


import com.zhidejiaoyu.student.service.PhoneticSymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zdjy
 * @since 2019-05-17
 */
@RestController
@RequestMapping("/phoneticSymbol")
public class PhoneticSymbolController {

    @Autowired
    private PhoneticSymbolService phoneticSymbolService;

    /**
     * 获取单元
     */
    @RequestMapping("getSymbolUnit")
    public Object getSymbolUnit(HttpSession session){
        return phoneticSymbolService.getSymbolUnit(session);
    }

    /**
     * 获取单元
     */
    @RequestMapping("getSymbol")
    public Object getSymbol(Integer unitId,HttpSession session){
        return phoneticSymbolService.getSymbol(unitId,session);
    }




}

