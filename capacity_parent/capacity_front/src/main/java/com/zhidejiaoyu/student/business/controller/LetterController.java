package com.zhidejiaoyu.student.business.controller;


import com.zhidejiaoyu.common.pojo.Letter;
import com.zhidejiaoyu.common.pojo.LetterPair;
import com.zhidejiaoyu.common.pojo.Player;
import com.zhidejiaoyu.student.business.service.LetterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 字母相关 controller
 *
 * @author zdjy
 * @since 2019-05-17
 */
@Slf4j
@RestController
@RequestMapping("/letter")
public class LetterController {

    @Autowired
    private LetterService letterService;

    /**
     * 获取字母单元
     */
    @RequestMapping("/getLetterUnit")
    public Object getLetterUnit(HttpSession session) {
        return letterService.getLetterUnit(session);
    }

    /**
     * 获取单元详细信息
     *
     * @param session
     * @param unitId
     * @return
     */
    @RequestMapping("/getLetterUnitStatus")
    public Object getLetterUnitStatus(HttpSession session, Long unitId) {
        return letterService.getLetterUnitStatus(session, unitId);
    }

    /**
     * 获取字母播放器数据
     *
     * @param unitId
     * @param session
     * @return
     */
    @RequestMapping("/getLetterListen")
    public Object getLetterListen(Long unitId, HttpSession session) {

        return letterService.getLetterListen(unitId, session);
    }

    /**
     * 保存字母试听
     *
     * @param player
     * @param session
     * @return
     */
    @RequestMapping("/saveLetterListen")
    public Object saveLetterListen(Player player, HttpSession session) {
        return letterService.saveLetterListen(player, session);
    }

    /**
     * 获取字母配对
     *
     * @param unitId
     * @param session
     * @return
     */
    @RequestMapping("/getLetterPair")
    public Object getLetterPair(Long unitId, HttpSession session) {
        return letterService.getLetterPair(unitId, session);
    }

    /**
     * 去除字母配对内容
     *
     * @param unitId
     * @param session
     * @return
     */
    @RequestMapping("/updLetterPair")
    public Object updLetterPair(Long unitId, HttpSession session) {
        return letterService.updLetterPair(session, unitId);
    }

    /**
     * 保存字母配对
     *
     * @param letterPair
     * @param session
     * @return
     */
    @RequestMapping("/saveLetterPair")
    public Object saveLetterPair(LetterPair letterPair, HttpSession session,Boolean falg) {
        return letterService.saveLetterPair(letterPair, session,falg);
    }

    /**
     * 获取字母宝典内容
     *
     * @param major
     * @param subordinate
     * @return
     */
    @RequestMapping("/getLetterTreasure")
    public Object getLetterTreasure(String major, String subordinate) {
        return letterService.getLetterTreasure(major, subordinate);
    }

    /**
     * 查看字母听写内容
     */
    @RequestMapping("/getLetterWrite")
    public Object getLetterWrite(Long unitId, HttpSession session) {
        return letterService.getLetterWrite(unitId, session);
    }

    /**
     * 去除字母听写数据
     */
    @RequestMapping("/updLetter")
    public Object updLetter(HttpSession session ,Long unitId){
        return letterService.updLetter(session,unitId);
    }

    /**
     * 保存字母听写内容
     */
    @RequestMapping("/saveLetterWrite")
    public Object saveLetterWrite(Letter letter, HttpSession session,Boolean falg) {

        return letterService.saveLetterWrite(letter,session,falg);
    }

    /**
     * 修改单元学习信息
     * @param unitId
     * @param type
     * @param session
     * @return
     */
    @PostMapping("/updLetterSymbolStudyModel")
    public Object updLetterSymbolStudyModel(Long unitId,Integer type,HttpSession session){
        return letterService.updLetterSymbolStudyModel(unitId,type,session);
    }
}

