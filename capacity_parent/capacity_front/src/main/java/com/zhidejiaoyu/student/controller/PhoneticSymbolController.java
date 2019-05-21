package com.zhidejiaoyu.student.controller;


import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.dto.phonetic.UnitTestDto;
import com.zhidejiaoyu.student.service.PhoneticSymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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
     * 获取音标辨音题目
     *
     * @param unitId 单元 id
     * @return
     */
    @GetMapping("/getSymbolListen")
    public ServerResponse<Object> getSymbolListen(Long unitId, HttpSession session) {
        if (unitId == null) {
            return ServerResponse.createByError(400, "单元 id 不能为空！");
        }
        return phoneticSymbolService.getSymbolListen(unitId, session);
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
     * 保存单元闯关测试记录
     *
     * @param session
     * @param dto
     * @return
     */
    @PostMapping("/saveUnitTest")
    public ServerResponse saveUnitTest(HttpSession session, @Valid UnitTestDto dto) {
        return phoneticSymbolService.saveUnitTest(session, dto);
    }

}

