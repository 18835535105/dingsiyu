package com.zhidejiaoyu.student.listener;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @author wuchenxi
 * @date 2018/6/19 15:31
 */
@Component
@WebListener
public class LoginOutListener implements HttpSessionListener {

    @Autowired
    private LoginService loginService;

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
        clearRedisSessionId(session);
        loginService.saveDuration(session);
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
}
