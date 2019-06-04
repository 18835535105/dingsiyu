package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.BaseTest;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author wuchenxi
 * @date 2018/6/30
 */
public class BookControllerTest extends BaseTest {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void test () throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
    }
}
