package com.zhidejiaoyu.student.business.service.simple;


import com.zhidejiaoyu.common.vo.gauntlet.GauntletSortVo;
import com.zhidejiaoyu.common.vo.simple.StudentGauntletVo;
import com.zhidejiaoyu.common.pojo.Gauntlet;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 * 挑战书表； 服务类
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-15
 */
public interface SimpleIGauntletServiceSimple extends SimpleBaseService<Gauntlet> {

    ServerResponse<Map<String,Object>> getStudentByType(HttpSession session, Integer type, Integer page, Integer row, String account, GauntletSortVo vo);

    ServerResponse<StudentGauntletVo> getStudyInteger(HttpSession session);

    ServerResponse<Object> addPkRecord(HttpSession session, String gameName, Long studentId, Integer gold, Long courseId, String challengerMsg);

    ServerResponse<Object> getGame(Integer pageNum, Long courseId, String gameName, HttpSession session,int type);

    ServerResponse<Object> getCourse(HttpSession session);

    ServerResponse<Object> getChallenge(Integer type, Integer pageNum, Integer rows, HttpSession session);

    void getStudy();

    ServerResponse<Object> getInformation(Long gauntletId, Integer type);

    ServerResponse<Object> saveResult(Long gauntletId, Integer type, Integer isDelete, Integer point, String concede);

    ServerResponse<Object> getChallengeInformation(HttpSession session);

    ServerResponse<Object> getPersonalPkData(HttpSession session, Integer page, Integer rows, Integer type);

    ServerResponse<Object> removeGauntlet(Long gauntletId);

    ServerResponse<Object> getReceiveChallenges(HttpSession session);

    ServerResponse<Object> closePkExplain(HttpSession session);

    ServerResponse<Object> getHeroList(HttpSession session, Integer type);

    ServerResponse<Object> getRank(HttpSession session,Integer type);
}
