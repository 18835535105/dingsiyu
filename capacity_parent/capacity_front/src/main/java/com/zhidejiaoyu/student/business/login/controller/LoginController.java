package com.zhidejiaoyu.student.business.login.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.login.service.LoginService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wuchenxi
 * @Date: 2019/12/2 15:39
 */
@RestController(value = "newLoginController")
@RequestMapping("/login")
public class LoginController extends BaseController {

    @Resource
    private LoginService newLoginService;

    /**
     * 登陆
     *
     * @param account  账号
     * @param password 密码
     * @return
     */
    @PostMapping("/judge")
    public ServerResponse<Object> loginJudge(String account, String password) {
        if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
            return ServerResponse.createByErrorMessage("用户名和密码不能为空！");
        }
        account = account.trim();
        password = password.trim();
        return newLoginService.loginJudge(account, password);

    }

    /**
     * 登录拦截，若未登录返回false，前端控制跳转到登录页面
     *
     * @param flag
     * @return
     */
    @RequestMapping("/toLogin/{boolean}")
    public Map<String, Boolean> isLogin(@PathVariable("boolean") Boolean flag) {
        Map<String, Boolean> isLogin = new HashMap<>(16);
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
    public void loginOut(HttpSession session) {
        session.invalidate();
    }

    /**
     * 学生退出系统，教师输入账号确认
     *
     * @param session
     * @param teacherAccount 教师账号
     * @return
     */
    @GetMapping("/isLoginOut")
    public Object isLoginOut(HttpSession session, String teacherAccount) {
        return newLoginService.isLoginOut(session, teacherAccount);
    }
}
