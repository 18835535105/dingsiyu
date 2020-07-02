package com.zhidejiaoyu.student.filter;

import com.zhidejiaoyu.common.utils.MacIpUtil;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.pojo.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author wuchenxi
 * @date 2018/6/21 20:03
 */
@Slf4j
@Component
@WebFilter(urlPatterns = "/*")
public class LoginFilter implements Filter {

    /**
     * 需要忽略过滤的路径
     */
    private static final Map<String, String> URL_MAP;

    /**
     * 路径中如果包含该数组里的字段，放行
     */
    private static final String[] INCLUDE_URL_ARR = new String[]{"/druid", "/smallApp", "/translate", "/publicAccount",
            "/clientValidity", "/qy", "actuator"};

    static {
        URL_MAP = new HashMap<>(16);
        URL_MAP.put("/login/judge", "/login/judge");
        URL_MAP.put("/login/loginOut", "/login/loginOut");
        URL_MAP.put("/login/validateCode", "/login/validateCode");
        URL_MAP.put("/ec/login/judge", "/ec/login/judge");
        URL_MAP.put("/ec/login/loginOut", "/ec/login/loginOut");
        URL_MAP.put("/ec/login/validateCode", "/ec/login/validateCode");

        URL_MAP.put("/ec/quartz/robot/getDailyState", "/ec/quartz/robot/getDailyState");
        URL_MAP.put("/quartz/robot/getDailyState", "/quartz/robot/getDailyState");
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpSession session = httpServletRequest.getSession();

        Student student = null;
        if (session.getAttribute(UserConstant.CURRENT_STUDENT) != null) {
            student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        }

        String url = httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length());
        // 如果是feign请求直接放行
        String feignFlag = httpServletRequest.getHeader("feign");
        if (log.isDebugEnabled()) {
            log.debug("feignFlag={}", feignFlag);
        }
        if (URL_MAP.containsKey(url) || checkIgnoreInclude(url) || StringUtils.isNotEmpty(feignFlag)) {
            // 不拦截登录和退出接口
            doFilter(chain, httpServletRequest, httpServletResponse, url);

        } else if (student != null) {
            // 学生已登录需要判断当前账号是否在其他地点登录
            judgeOtherLocation(chain, httpServletRequest, httpServletResponse, session, student, url);
        } else {
            // 未登录状态访问其他接口
            request.getRequestDispatcher("/login/toLogin/false").forward(request, response);
        }

    }

    private boolean checkIgnoreInclude(String url) {
        for (String s : INCLUDE_URL_ARR) {
            if (url.contains(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 放行
     *
     * @param chain
     * @param httpServletRequest
     * @param httpServletResponse
     * @param url
     * @throws ServletException
     * @throws IOException
     */
    private void doFilter(FilterChain chain, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String url) throws ServletException, IOException {
        if (url.contains("/ec")) {
            httpServletRequest.getRequestDispatcher(url.substring(3)).forward(httpServletRequest, httpServletResponse);
        } else {
            chain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    private void judgeOtherLocation(FilterChain chain, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    HttpSession session, Student student, String url) throws ServletException, IOException {
        replaceSession(httpServletRequest, student);
        // 当前学生已登录，判断是否其他地点也有登录
        if (!toMultipleLoginTip(httpServletRequest, httpServletResponse, session)) {
            // 其他地点没有登录，放行
            doFilter(chain, httpServletRequest, httpServletResponse, url);
        }
    }

    /**
     * 如果当前账号同时在其他地点登录，给用户提示信息
     *
     * @param request
     * @param response
     * @param session
     * @return <code>true</code>账号同时在其他地点登录 <code>false</code> 其他情况
     * @throws ServletException
     * @throws IOException
     */
    private Boolean toMultipleLoginTip(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        Object msg = session.getAttribute("multipleLoginMsg");
        if (msg != null) {
            request.getRequestDispatcher("/login/multipleLogin/" + msg).forward(request, response);
            return true;
        }
        return false;
    }

    /**
     * 如果学生账号在另一地点登录，向当前登录session中放置提示信息
     *
     * @param request
     * @param student
     */
    private void replaceSession(HttpServletRequest request, Student student) {
        Long id = student.getId();
        Object sessionObject = redisTemplate.opsForHash().get(RedisKeysConst.LOGIN_SESSION, id);
        if (sessionObject == null) {
            return;
        }

        String sessionId = (String) sessionObject;
        HttpSession currentSession = request.getSession();
        currentSession.removeAttribute("multipleLoginMsg");
        String currentSessionId = currentSession.getId();

        // 当 loginSession 的值为-1 时，说明是管理员从后台强制该学生下线
        String flag = "-1";
        if (Objects.equals(flag, sessionObject.toString())) {
            currentSession.setAttribute("multipleLoginMsg", "系统检测到你异地登录，被强制下线！");
            return;
        }

        // 当 loginSession 的值为-2 时，说明是该学生信息被清除，需要重新登录
        flag = "-2";
        if (Objects.equals(flag, sessionObject.toString())) {
            currentSession.setAttribute("multipleLoginMsg", "该账号信息已被清除，已被强制下线！");
            return;
        }

        if (!Objects.equals(sessionId, currentSessionId)) {
            try {
                String ip = MacIpUtil.getIpAddr(request);
                log.error("学生[{}]->[{}] 在ip=[{}]地方异地登录；", student.getId(), student.getStudentName(), ip);
            } catch (Exception e) {
                log.error("获取学生IP地址失败, error=[{}]", e.getMessage());
            }
            currentSession.setAttribute("multipleLoginMsg", "您的帐号在另一地点登录，您已被迫下线！请重新登录。");
        }
    }

    @Override
    public void destroy() {

    }

}
