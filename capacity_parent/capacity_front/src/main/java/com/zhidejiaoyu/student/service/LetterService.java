package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.Letter;
import com.baomidou.mybatisplus.service.IService;
import com.zhidejiaoyu.common.pojo.LetterPair;
import com.zhidejiaoyu.common.pojo.Player;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zdjy
 * @since 2019-05-17
 */
public interface LetterService extends IService<Letter> {

    Object getLetterUnit(HttpSession session);

    Object getLetterUnitStatus(HttpSession session, Long unitId);

    Object getLetterListen(Long unitId, HttpSession session);

    Object saveLetterListen(Player player, HttpSession session);

    Object getLetterPair(Long unitId, HttpSession session);

    Object saveLetterPair(LetterPair letterPair, HttpSession session,Boolean falg);

    Object getLetterTreasure(String major, String subordinate);

    Object getLetterWrite(Long unitId, HttpSession session);

    Object saveLetterWrite(Letter letter, HttpSession session,Boolean falg);

    Object updLetter(HttpSession session, Long unitId);

    Object updLetterSymbolStudyModel(Long unitId, Integer type, HttpSession session);
}
