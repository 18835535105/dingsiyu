package com.zhidejiaoyu.student.business.controller.simple;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.simple.SimpleLoginServiceSimple;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
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
@RequestMapping("/api/login")
@Slf4j
public class SimpleLoginController {

    /**
     * 登陆业务层接口
     */
    @Autowired
    private SimpleLoginServiceSimple loginService;

    /**
     * 首页数据 - 精简版首页
     *
     * @param session
     * @return 首页需要展示的数据
     */
    @PostMapping("/simpleIndex")
    public ServerResponse<Object> indexDate(HttpSession session) {
        return loginService.index(session);
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
            log.warn("学生 {}->{} 试图修改学生 {} 的密码！", student.getId(), student.getStudentName(), studentId);
            return ServerResponse.createByErrorMessage("无权限修改他人密码！");
        }
        if (!Objects.equals(student.getPassword(), oldPassword)) {
            return ServerResponse.createByErrorMessage("原密码输入错误！");
        }
        return loginService.updatePassword(oldPassword, password, session);
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

}
