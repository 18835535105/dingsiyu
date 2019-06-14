package com.zhidejiaoyu.student.listener;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.mapper.simple.SimpleRunLogMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.config.ServiceInfoUtil;
import com.zhidejiaoyu.student.service.impl.LoginServiceImpl;
import com.zhidejiaoyu.student.service.simple.impl.SimpleLoginServiceImplSimple;
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
    private SimpleLoginServiceImplSimple loginService;

    @Autowired
    private SimpleRunLogMapper runLogMapper;

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
            saveLogoutInfo(session);
            Map<String, Object> sessionMap = null;
            Object object = redisTemplate.opsForHash().get(RedisKeysConst.SESSION_MAP, session.getId());
            if (object != null) {
                sessionMap = (Map<String, Object>) object;
            }
            loginService.saveDurationInfo(sessionMap);
        }
    }

    /**
     * session 过期时记录学生退出信息
     *
     * @param session
     */
    private void saveLogoutInfo(HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (student != null) {
            SimpleLoginServiceImplSimple.saveLogoutLog(student, runLogMapper, log);
        }
    }
}
