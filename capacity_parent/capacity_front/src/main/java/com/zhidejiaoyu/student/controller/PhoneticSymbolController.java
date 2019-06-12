package com.zhidejiaoyu.student.controller;


import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.PhoneticSymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zdjy
 * @since 2019-05-17
 */
@Validated
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

    /**
     * 获取音节辨音题目
     *
     * @param unitId 单元 id
     * @param restudy 是否重新学习
     * @return
     */
    @GetMapping("/getSymbolListen")
    public ServerResponse<Object> getSymbolListen(Long unitId, HttpSession session,
                                                  @RequestParam(required = false, defaultValue = "false") Boolean restudy) {
        if (unitId == null) {
            return ServerResponse.createByError(400, "单元 id 不能为空！");
        }
        return phoneticSymbolService.getSymbolListen(unitId, session, restudy);
    }

    /**
     * 保存音节辨音记录
     *
     * @param session
     * @param unitId
     * @param symbolId  音节 id
     * @return
     */
    @PostMapping("/saveSymbolListen")
    public ServerResponse saveSymbolListen(HttpSession session, Long unitId,  Integer symbolId) {
        if (unitId == null || symbolId == null) {
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        return phoneticSymbolService.saveSymbolListen(session, unitId, symbolId);
    }

    /**
     * 获取单元闯关测试题目
     *
     * @param session
     * @param unitId
     * @return
     */
    @GetMapping("/getUnitTest")
    public ServerResponse getUnitTest(HttpSession session, Long unitId) {
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        return phoneticSymbolService.getUnitTest(session, unitId);
    }

    /**
     * 获取所有音标读音
     */
    @GetMapping("/getAllSymbolListen")
    public ServerResponse getAllSymbolListen(){
        return phoneticSymbolService.getAllSymbolListen();
    }

}

