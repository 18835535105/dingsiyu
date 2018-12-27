package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 用户登陆(学生)
 *
 * @author qizhentao
 * @version 1.0
 */
@RestController
@RequestMapping("/login")
@Slf4j
public class LoginController {

    /**
     * 登陆业务层接口
     */
    @Autowired
    private LoginService loginService;

    /**
     * 登陆
     *
     * @param account  账号
     * @param password 密码
     * @param code 验证码
     * @param session
     * @return
     */
    @RequestMapping(value = "/judge", method = RequestMethod.POST)
    public ServerResponse loginJudge(String account, String password, HttpSession session, String code) {
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            return ServerResponse.createByErrorMessage("用户名和密码不能为空！");
        }
        return loginService.loginJudge(account, password, session, code);

    }

    /**
     * 首页数据(单词首页)
     *
     * @param overReview true:智能复习节点完成；false：智能复习节点未完成
     * @return 首页需要展示的数据
     */
    @RequestMapping("/vocabularyIndex")
    public ServerResponse<Object> indexDate(HttpSession session, @RequestParam(required = false, defaultValue = "false") Boolean overReview) {
        // 进入单词首页时如果需要智能复习，该标识记录需要智能复习的模块名称，智能复习节点完成后清除该节点
        if (overReview) {
            session.removeAttribute("needReview");
        }
        return loginService.index(session);
    }

    /**
     * 首页数据(例句首页)
     *
     * @return 首页需要展示的数据
     */
    @RequestMapping("/sentenceIndex")
    public ServerResponse<Object> sentenceIndex(HttpSession session) {
        return loginService.sentenceIndex(session);
    }


    /**
     * 修改密码
     *
     * @param oldPassword  旧密码
     * @param password 新密码
     */
    @RequestMapping("/updatePassword")
    public ServerResponse<String> updatePassword(String oldPassword, String password, HttpSession session, Long studentId) {
        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(password)) {
            return ServerResponse.createByErrorMessage("密码不能为空！");
        }
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (!Objects.equals(student.getId(), studentId)) {
            log.error("学生 {}->{} 试图修改学生 {} 的密码！", student.getId(), student.getStudentName(), studentId);
            return ServerResponse.createByErrorMessage("无权限修改他人密码！");
        }
        if (!Objects.equals(student.getPassword(), oldPassword)) {
            return ServerResponse.createByErrorMessage("原密码输入错误！");
        }
        return loginService.updatePassword(password, session);
    }

    /**
     * 首页点击头像
     *
     */
    @RequestMapping("/portrait")
    public ServerResponse<Object> clickPortrait(HttpSession session){
        return loginService.clickPortrait(session);
    }

    /**
     * 登录拦截，若未登录返回false，前端控制跳转到登录页面
     *
     * @param flag
     * @return
     */
    @RequestMapping("/toLogin/{boolean}")
    public Map<String, Boolean> isLogin(@PathVariable("boolean") Boolean flag) {
        Map<String, Boolean> isLogin = new HashMap<>();
        isLogin.put("isLogin", flag);
        return isLogin;
    }

    /**
     * 多地点同时登录提示语
     * <p>当用户通过不同session登录时，新登录的sessionId会覆盖旧sessionId，此时旧session用户会收到一条提示信息</p>
     *
     * @param msg 账号在另一地点登录提示信息
     * @return
     */
    @RequestMapping("/multipleLogin/{msg}")
    public Map<String, String> multipleLogin(@PathVariable("msg") Object msg) {
        Map<String, String> map = new HashMap<>(16);
        map.put("multipleLogin", msg.toString());
        return map;
    }

    /**
     * 学生退出
     *
     * @param session
     */
    @PostMapping("/loginOut")
    public void loginOut(HttpSession session, HttpServletRequest request) {
        loginService.loginOut(session, request);
    }

    /**
     * 生成验证码
     */
    @PostMapping("/validateCode")
    public void getValidateCode(HttpSession session, HttpServletResponse response) throws IOException {
        loginService.getValidateCode(session, response);
    }
}
