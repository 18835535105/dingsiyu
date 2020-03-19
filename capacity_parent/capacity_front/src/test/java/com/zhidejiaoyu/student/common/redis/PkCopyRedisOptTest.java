package com.zhidejiaoyu.student.common.redis;

import com.zhidejiaoyu.common.pojo.PkCopyBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/3/19 10:27:27
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class PkCopyRedisOptTest {

    @Resource
    private PkCopyRedisOpt pkCopyRedisOpt;

    @Test
    public void saveSchoolCopyStudentInfo() {
    }

    @Test
    public void getSchoolCopyStudentInfo() {
    }

    @Test
    public void markSchoolCopyAward() {
    }

    @Test
    public void judgeSchoolCopyAward() {
    }

    @Test
    public void getPkCopyBaseById() {
        PkCopyBase pkCopyBaseById = pkCopyRedisOpt.getPkCopyBaseById(5L);
        log.info(pkCopyBaseById.toString());
    }
}
