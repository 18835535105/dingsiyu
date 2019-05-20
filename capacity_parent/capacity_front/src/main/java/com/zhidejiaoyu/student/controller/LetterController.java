package com.zhidejiaoyu.student.controller;


import com.zhidejiaoyu.common.pojo.LetterPair;
import com.zhidejiaoyu.common.pojo.Player;
import com.zhidejiaoyu.student.service.LetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/letter")
public class LetterController {

    @Autowired
    private LetterService letterService;

    /**
     * 获取字母单元
     */
    @RequestMapping("/getLetterUnit")
    public Object getLetterUnit(HttpSession session){
        return letterService.getLetterUnit(session);
    }

    /**
     * 获取单元详细信息
     * @param session
     * @param unitId
     * @return
     */
    @RequestMapping("/getLetterUnitStatus")
    public Object getLetterUnitStatus(HttpSession session,Long unitId){
        return letterService.getLetterUnitStatus(session,unitId);
    }

    /**
     * 获取字母播放器数据
     * @param unitId
     * @param session
     * @return
     */
    @RequestMapping("/getLetterListen")
    public Object getLetterListen(Long unitId,HttpSession session){
        return letterService.getLetterListen(unitId,session);
    }

    /**
     * 保存字母试听
     * @param player
     * @param session
     * @return
     */
    @RequestMapping("/saveLetterListen")
    public Object saveLetterListen(Player player, HttpSession session){
        return letterService.saveLetterListen(player,session);
    }

    /**
     * 获取字母配对
     * @param unitId
     * @param session
     * @return
     */
    @RequestMapping("/getLetterPair")
    public Object getLetterPair(Long unitId,HttpSession session){
        return letterService.getLetterPair(unitId,session);
    }

    /**
     * 保存字母配对
     * @param letterPair
     * @param session
     * @return
     */
    @RequestMapping("/saveLetterPair")
    public Object saveLetterPair(LetterPair letterPair,HttpSession session){
        return letterService.saveLetterPair(letterPair,session);
    }

    /**
     * 获取字母宝典内容
     * @param major
     * @param subordinate
     * @return
     */
    @RequestMapping("/getLetterTreasure")
    public Object getLetterTreasure(String major,String subordinate){
        return letterService.getLetterTreasure(major,subordinate);
    }

}

