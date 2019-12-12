package com.zhidejiaoyu.student.listener;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.RunLogMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.login.service.LoginService;
import com.zhidejiaoyu.student.login.service.impl.LoginServiceImpl;
import com.zhidejiaoyu.student.utils.ServiceInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author wuchenxi
 * @date 2018/6/19 15:31
 */
@Slf4j
@Component
@WebListener
public class SessionListener implements HttpSessionListener {

    @Value("${quartz.port}")
    private String port;

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
    @SuppressWarnings("all")
    public void sessionDestroyed(HttpSessionEvent se) {
        int currentPort = ServiceInfoUtil.getPort();
        // 当 session 失效时只有 8082 端口的服务负责保存学生时长信息
        if (currentPort == Integer.valueOf(port)) {
            HttpSession session = se.getSession();
            if (checkMultipleLoginSession(session)) {
                String key = RedisKeysConst.MULTIPLE_LOGIN_SESSION_ID + session.getId();
                redisTemplate.opsForValue().set(key, null);
                redisTemplate.expire(key, 40, TimeUnit.MINUTES);
                return;
            }

            saveLogoutInfo(session);
            Map<String, Object> sessionMap = null;
            Object object = redisTemplate.opsForHash().get(RedisKeysConst.SESSION_MAP, session.getId());
            if (object != null) {
                sessionMap = (Map<String, Object>) object;
                Student student = (Student) sessionMap.get(UserConstant.CURRENT_STUDENT);
                // 学生 session 失效时将该学生从在线人数中移除
                redisTemplate.opsForZSet().remove(RedisKeysConst.ZSET_ONLINE_USER, student.getId());
            }
            loginService.saveDurationInfo(sessionMap);
        }
    }

    /**
     * 判断当前 session 是否是学生异地登录时被挤掉的 session
     *
     * @param session
     * @return true：是被挤掉的 session <br>
     * false：是正常的 session
     */
    private boolean checkMultipleLoginSession(HttpSession session) {
        return Objects.equals(redisTemplate.opsForValue().get(RedisKeysConst.MULTIPLE_LOGIN_SESSION_ID + session.getId()), session.getId());
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
}
