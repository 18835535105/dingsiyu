package com.zhidejiaoyu.student.business.feignclient.course;

import com.zhidejiaoyu.ZdjyFrontApplication;
import com.zhidejiaoyu.common.pojo.CourseNew;
import com.zhidejiaoyu.common.pojo.TeksNew;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 15:21:21
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class CourseFeignClientTest {

    @Resource
    private CourseFeignClient courseFeignClient;

    @Test
    public void testGetById() {
        CourseNew courseNew = courseFeignClient.getById(2749L);
        log.info("course={}", courseNew.toString());
    }

    @Test
    public void selTeksByUnitIdAndGroup() {
        List<TeksNew> teksNews = courseFeignClient.selTeksByUnitIdAndGroup(101033L, 1);
        log.info("teksNews={}", teksNews.toString());
    }
}
