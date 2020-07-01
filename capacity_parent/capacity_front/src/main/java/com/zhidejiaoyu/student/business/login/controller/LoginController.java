package com.zhidejiaoyu.student.business.login.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.currentdayofstudy.StudyTimeAndMileageVO;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.currentDayOfStudy.service.CurrentDayOfStudyService;
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

    @Resource
    private CurrentDayOfStudyService currentDayOfStudyService;

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
    public ServerResponse<Object> loginOut(HttpSession session) {
        StudyTimeAndMileageVO todayInfo = currentDayOfStudyService.getTodayInfo();
        if (todayInfo.getMileage() < 4 || todayInfo.getTime() / 60 < 45) {
            // 小于4个里程或者在线时长小于45分钟不让退出
            return ServerResponse.createByError(406, "飞行任务还没有完成，请集中注意力，继续飞行！");
        }

        session.invalidate();
        return ServerResponse.createBySuccess();
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

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param password    新密码
     */
    @RequestMapping("/updatePassword")
    public ServerResponse<String> updatePassword(String oldPassword, String password, HttpSession session, Long studentId) {
        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(password)) {
            return ServerResponse.createByErrorMessage("密码不能为空！");
        }
        return newLoginService.updatePassword(password, session, oldPassword, studentId);
    }
}
