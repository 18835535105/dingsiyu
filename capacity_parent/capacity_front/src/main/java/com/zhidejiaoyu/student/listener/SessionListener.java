package com.zhidejiaoyu.student.listener;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.RunLogMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.service.LoginService;
import com.zhidejiaoyu.student.service.impl.LoginServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;

/**
 * @author wuchenxi
 * @date 2018/6/19 15:31
 */
@Slf4j
@Component
@WebListener
public class SessionListener implements HttpSessionListener {

    /**
     * 存放登陆过系统的sessionId 和 session的对应关系,当学生有多地登录行为时清除 sessionMap 中的学生信息
     */
    private static HashMap<String, HttpSession> sessionMap = new HashMap<>(16);

    @Autowired
    private LoginService loginService;

    @Autowired
    private RunLogMapper runLogMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Receives notification that a session has been created.
     *
     * @param se the HttpSessionEvent containing the session
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    /**
     * Receives notification that a session is about to be invalidated.
     *
     * @param se the HttpSessionEvent containing the session
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        removeSessionById(session.getId());
        clearRedisSessionId(session);
        saveLogoutInfo(session);
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        loginService.saveDurationInfo(student, session);
    }

    /**
     * session 过期时记录学生退出信息
     *
     * @param session
     */
    private void saveLogoutInfo(HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (student != null) {
            LoginServiceImpl.saveLogoutLog(student, runLogMapper, log);
        }
    }

    /**
     * 清除redis中用于判断用户是否多地点同时登录的sessionId标识
     *
     * @param session
     */
    private void clearRedisSessionId(HttpSession session) {
        Object studentSession = session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (studentSession != null) {
            Student student = (Student) studentSession;
            redisTemplate.opsForHash().delete("loginSession", student.getId());
        }
    }

    /**
     * 通过sessionid获取session信息
     *
     * @param sessionId
     * @return
     */
    public static HttpSession getSessionById(String sessionId) {
        return sessionMap.get(sessionId);
    }

    /**
     * 根据sessionId 删除指定的session信息
     *
     * @param sessionId
     */
    public static void removeSessionById(String sessionId) {
        sessionMap.remove(sessionId);
    }

    /**
     * 增加session
     *
     * @param session
     */
    public static void putSession(HttpSession session) {
        sessionMap.put(session.getId(), session);
    }
}
