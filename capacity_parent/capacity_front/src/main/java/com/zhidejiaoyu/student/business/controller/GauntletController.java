package com.zhidejiaoyu.student.business.controller;

import com.zhidejiaoyu.common.vo.gauntlet.GauntletSortVo;
import com.zhidejiaoyu.common.vo.simple.StudentGauntletVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.simple.SimpleIGauntletServiceSimple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 控制器
 *
 * @author zdjy
 * @Date 2019-03-15 13:33:53
 */
@Slf4j
@RestController
@RequestMapping("/gauntlet")
public class GauntletController {

    @Autowired
    private SimpleIGauntletServiceSimple gauntletService;

    /**
     * 查询挑战的学生
     *
     * @param session
     * @param type    1,本班  2,本校
     * @return
     */
    @RequestMapping("/getStudentByType")
    public ServerResponse<Map<String, Object>> getStudentByType(HttpSession session, @RequestParam(defaultValue = "1") Integer type,
                                                                @RequestParam(defaultValue = "1") Integer pageNum, Integer rows,
                                                                String account, GauntletSortVo vo) {
        return gauntletService.getStudentByType(session, type, pageNum, rows, account, vo);
    }


    /**
     * 获取详情
     *
     * @param session session
     * @return
     */
    @RequestMapping("/getChallengeInformation")
    public ServerResponse<Object> getChallengeInformation(HttpSession session) {
        return gauntletService.getChallengeInformation(session);
    }

    /**
     * 查询自己的信息
     *
     * @param session
     * @return
     */
    @RequestMapping("/getStudyInteger")
    public ServerResponse<StudentGauntletVo> getStudyInteger(HttpSession session) {
        return gauntletService.getStudyInteger(session);
    }

    /**
     * 获取游戏信息
     *
     * @param pageNum  页数
     * @param courseId 课程id
     * @param gameName 游戏名称
     * @param session  session
     * @return
     */
    @RequestMapping("/getGame")
    public ServerResponse<Object> getGame(Integer pageNum, Long courseId, String gameName, HttpSession session, int type) {
        return gauntletService.getGame(pageNum, courseId, gameName, session, type);
    }

    /**
     * 获取课文
     *
     * @param session session
     * @return
     */
    @RequestMapping("/getCourse")
    public ServerResponse<Object> getCourse(HttpSession session) {
        return gauntletService.getCourse(session);
    }

    /**
     * 查看发起的挑战 和被挑战的次数
     *
     * @param type    1,发起的挑战  2,被挑战
     * @param pageNum 页数
     * @param rows    每页显示个数
     * @return
     */
    @RequestMapping("/getChallenge")
    public ServerResponse<Object> getChallenge(Integer type, Integer pageNum, Integer rows, HttpSession session) {
        return gauntletService.getChallenge(type, pageNum, rows, session);
    }


    /**
     * 批量生成账号  未使用
     */
    @RequestMapping("/getAllStudy")
    public void getAllStudy() {
        gauntletService.getStudy();
    }


    /**
     * 获取详情
     *
     * @param gauntletId 挑战id
     * @param type       0，全部   1，胜  2，失败 3，等待 4，超时  5，平局 6，认输
     * @return
     */
    @RequestMapping("/information")
    public ServerResponse<Object> information(Long gauntletId, Integer type) {
        return gauntletService.getInformation(gauntletId, type);
    }

    /**
     * @param gauntletId 比拼id
     * @param type       1，发起者保存  2，挑战者保存
     * @param isDelete   1，不删除  2，删除
     * @param point      分数
     * @return
     */
    @RequestMapping("/saveResults")
    public ServerResponse<Object> saveResults(Long gauntletId, Integer type, Integer isDelete, Integer point, String concede) {
        return gauntletService.saveResult(gauntletId, type, isDelete, point, concede);
    }

    /**
     * 获取个人战绩详情
     *
     * @param session session
     * @param page    页数
     * @param rows    每页显示个数
     * @return
     */
    @RequestMapping("/getPersonalPkData")
    public ServerResponse<Object> getPersonalPkData(HttpSession session, Integer page, Integer rows, Integer type) {
        return gauntletService.getPersonalPkData(session, page, rows, type);
    }

    /**
     * 去除其他数据
     */
    @RequestMapping("/removeGauntlet")
    public ServerResponse<Object> removeGauntlet(Long gauntletId) {
        return gauntletService.removeGauntlet(gauntletId);
    }

    /**
     * 添加数据
     */
    @RequestMapping("/getStudy")
    public void getStudy() {
        gauntletService.getStudy();
    }

    /**
     * 查看收到的挑战
     */
    @RequestMapping("/getReceiveChallenges")
    public ServerResponse<Object> getReceiveChallenges(HttpSession session) {
        return gauntletService.getReceiveChallenges(session);
    }

    /**
     * 关闭说明
     */
    @RequestMapping("/closePkExplain")
    public ServerResponse<Object> closePkExplain(HttpSession session) {
        return gauntletService.closePkExplain(session);
    }

    /**
     * 查询英雄榜数据
     *
     * @param session
     * @param type    1,本班  2，本校
     * @return
     */
    @RequestMapping("/getHeroList")
    public ServerResponse<Object> getHeroList(HttpSession session, Integer type) {
        return gauntletService.getHeroList(session, type);
    }

    /**
     * 查询pk排行榜，
     *
     * @param session
     * @param type    1，本班  2，本校  3，本区
     * @return
     */
    @RequestMapping("/getRank")
    public ServerResponse<Object> getRank(HttpSession session, Integer type) {
        return gauntletService.getRank(session, type);
    }


}
