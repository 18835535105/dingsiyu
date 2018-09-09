package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.utils.language.YouDaoTranslate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private YouDaoTranslate youDaoTranslate;

    @Test
    public void test () throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
    }

    @Test
    public void youDaoTranslateTest() {
        try {
            System.out.println(youDaoTranslate.getResultMap("hello"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}