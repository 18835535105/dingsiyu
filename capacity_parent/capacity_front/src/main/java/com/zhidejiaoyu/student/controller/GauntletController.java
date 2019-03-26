package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 控制器
 *
 * @author zdjy
 * @Date 2019-03-15 13:33:53
 */
@RestController
@RequestMapping("/gauntlet")
public class GauntletController {

    @Value("${domain}")
    private String domain;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 查询挑战的学生
     *
     * @param session
     * @param type    1,本班  2,本校
     * @return
     */
    @RequestMapping("/getStudentByType")
    public ServerResponse<Object> getStudentByType(HttpSession session, @RequestParam(defaultValue = "1") Integer type,
                                                                @RequestParam(defaultValue = "1") Integer page, Integer rows,
                                                                String account) {
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("type", type);
        paramMap.put("page", page);
        paramMap.put("rows", rows);
        paramMap.put("account", account);
        paramMap.put("session", session);
        paramMap.put("studentId", ((Student)session.getAttribute(UserConstant.CURRENT_STUDENT)).getId());
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));

        String url = domain + "/api/gauntlet/getStudentByType?type={type}&studentId={studentId}&page={page}&rows={rows}" +
                "&account={account}&session={session}&loginTime={loginTime}";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(responseEntity.getBody() == null ? null : responseEntity.getBody().get("data"));
    }


    /**
     * 查询自己的信息
     *
     * @param session
     * @return
     */
    @RequestMapping("/getStudyInteger")
    public ServerResponse<Object> getStudyInteger(HttpSession session) {
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("session", session);
        paramMap.put("studentId", ((Student)session.getAttribute(UserConstant.CURRENT_STUDENT)).getId());
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
        String url = domain + "/api/gauntlet/getStudyInteger?session={session}&studentId={studentId}&loginTime={loginTime}";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(responseEntity.getBody() == null ? null : responseEntity.getBody().get("data"));
    }

    @RequestMapping("/addPkRecord")
    public ServerResponse<Object> addPkRecord(HttpSession session, String gameName, Long studentId, Integer gold, Long courseId, String challengerMsg) {
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("gameName", gameName);
        paramMap.put("gold", gold);
        paramMap.put("courseId", courseId);
        paramMap.put("challengerMsg", challengerMsg);
        paramMap.put("session", session);
        paramMap.put("studentId", ((Student)session.getAttribute(UserConstant.CURRENT_STUDENT)).getId());
        paramMap.put("beStudentId",studentId);
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
        String url = domain + "/api/gauntlet/addPkRecord?gameName={gameName}&studentId={studentId}&gold={gold}&courseId={courseId}&challengerMsg={challengerMsg}&session={session}&beStudentId={beStudentId}&loginTime={loginTime}";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(responseEntity.getBody() == null ? null : responseEntity.getBody().get("data"));
    }

    @RequestMapping("/getGame")
    public ServerResponse<Object> getGame(Integer pageNum, Long courseId, String gameName, HttpSession session){
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("pageNum", pageNum);
        paramMap.put("courseId", courseId);
        paramMap.put("gameName", gameName);
        paramMap.put("session", session);
        paramMap.put("studentId", ((Student)session.getAttribute(UserConstant.CURRENT_STUDENT)).getId());
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
        String url = domain + "/api/gauntlet/getGame?gameName={gameName}&pageNum={pageNum}&courseId={courseId}&session={session}&studentId={studentId}&loginTime={loginTime}";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(responseEntity.getBody() == null ? null : responseEntity.getBody().get("data"));
    }

    @RequestMapping("/getCourse")
    public ServerResponse<Object> getCourse(HttpSession session){
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("session", session);
        paramMap.put("studentId", ((Student)session.getAttribute(UserConstant.CURRENT_STUDENT)).getId());
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
        String url = domain + "/api/gauntlet/getCourse?session={session}&studentId={studentId}&loginTime={loginTime}";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(responseEntity.getBody() == null ? null : responseEntity.getBody().get("data"));
    }

    /**
     * 查看发起的挑战 和被挑战的次数
     * @param type 1,发起的挑战  2,被挑战
     * @param challengeType 0，全部   1，胜  2，失败 3，等待 4，超时  5，平局
     * @param pageNum
     * @param rows
     * @return
     */
    @RequestMapping("/getChallenge")
    public ServerResponse<Object> getChallenge(Integer type, Integer challengeType, Integer pageNum, Integer rows, HttpSession session){
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("type", type);
        paramMap.put("challengeType", challengeType);
        paramMap.put("pageNum", pageNum);
        paramMap.put("rows", rows);
        paramMap.put("session", session);
        paramMap.put("studentId", ((Student)session.getAttribute(UserConstant.CURRENT_STUDENT)).getId());
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
        String url = domain + "/api/gauntlet/getChallenge?type={type}&challengeType={challengeType}&pageNum={pageNum}&rows={rows}&session={session}&studentId={studentId}&loginTime={loginTime}";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(responseEntity.getBody() == null ? null : responseEntity.getBody().get("data"));
    }

    /**
     * 查看发起的挑战 和被挑战的次数
     * @param gauntletId 挑战编号
     * @return
     */
    @RequestMapping("/information")
    public ServerResponse<Object> information(Long gauntletId , Integer type , HttpSession session){
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("gauntletId", gauntletId);
        paramMap.put("type", type);
        paramMap.put("session", session);
        paramMap.put("studentId", ((Student)session.getAttribute(UserConstant.CURRENT_STUDENT)).getId());
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
        String url = domain + "/api/gauntlet/information?type={type}&gauntletId={gauntletId}&session={session}&studentId={studentId}&loginTime={loginTime}";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(responseEntity.getBody() == null ? null : responseEntity.getBody().get("data"));
    }

    /**
     * 查看之前是否已经挑战过了
     * @param session
     * @return
     */
    @RequestMapping("/getChallengeInformation")
    public ServerResponse<Object> getChallengeInformation(HttpSession session){
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("session", session);
        paramMap.put("studentId", ((Student)session.getAttribute(UserConstant.CURRENT_STUDENT)).getId());
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
        String url = domain + "/api/gauntlet/getChallengeInformation?session={session}&studentId={studentId}&loginTime={loginTime}";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(responseEntity.getBody() == null ? null : responseEntity.getBody().get("data"));
    }

    /**
     *
     * @param gauntletId 比拼id
     * @param type  1，发起者报春  2，挑战者保存
     * @param isDelete 1，不删除  2，删除
     * @param point 分数
     * @return
     */
    @RequestMapping("/saveResults")
    public ServerResponse<Object> saveResults(Long gauntletId,Integer type,Integer isDelete,Integer point,HttpSession session,String concede){
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("gauntletId",gauntletId);
        paramMap.put("type",type);
        paramMap.put("concede",concede);
        paramMap.put("isDelete",isDelete);
        paramMap.put("point",point);
        paramMap.put("session", session);
        paramMap.put("studentId", ((Student)session.getAttribute(UserConstant.CURRENT_STUDENT)).getId());
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
        String url = domain + "/api/gauntlet/saveResults?concede={concede}&" +
                "point={point}&isDelete={isDelete}&gauntletId={gauntletId}&type={type}&session={session}&studentId={studentId}&loginTime={loginTime}";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(responseEntity.getBody() == null ? null : responseEntity.getBody().get("data"));
    }
    /**
     * 获取个人战绩详情
     * @param session
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/getPersonalPkData")
    public ServerResponse<Object> getPersonalPkData(HttpSession session,Integer page,Integer rows,Integer type){
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("page",page);
        paramMap.put("type",type);
        paramMap.put("rows",rows);
        paramMap.put("session", session);
        paramMap.put("studentId", ((Student)session.getAttribute(UserConstant.CURRENT_STUDENT)).getId());
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
        String url = domain + "/api/gauntlet/getPersonalPkData?type={type}&page={page}&rows={rows}&session={session}&studentId={studentId}&loginTime={loginTime}";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(responseEntity.getBody() == null ? null : responseEntity.getBody().get("data"));
    }

    /**
     * 去除其他数据
     */
    @RequestMapping("/removeGauntlet")
    public ServerResponse<Object> removeGauntlet(Long gauntletId,HttpSession session){
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("gauntletId",gauntletId);
        paramMap.put("session", session);
        paramMap.put("studentId", ((Student)session.getAttribute(UserConstant.CURRENT_STUDENT)).getId());
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
        String url = domain + "/api/gauntlet/removeGauntlet?gauntletId={gauntletId}&session={session}&studentId={studentId}&loginTime={loginTime}";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(responseEntity.getBody() == null ? null : responseEntity.getBody().get("data"));
    }

    /**
     * 查看收到的挑战数量
     */
    @RequestMapping("/getReceiveChallenges")
    public ServerResponse<Object> getReceiveChallenges(HttpSession session){
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("session", session);
        paramMap.put("studentId", ((Student)session.getAttribute(UserConstant.CURRENT_STUDENT)).getId());
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
        String url = domain + "/api/gauntlet/getReceiveChallenges?session={session}&studentId={studentId}&loginTime={loginTime}";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(responseEntity.getBody() == null ? null : responseEntity.getBody().get("data"));
    }


}
