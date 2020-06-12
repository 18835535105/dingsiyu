package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.ZdjyFrontApplication;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/5/21 14:18:18
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class PkCopyBaseMapperTest {

    @Resource
    private PkCopyBaseMapper pkCopyBaseMapper;

    @Test
    public void selectSchoolPkBaseInfoByType() {
        List<Map<String, Object>> pkBaseInfoVOS = pkCopyBaseMapper.selectSchoolPkBaseInfoByCount(100);
        log.info("schoolPkBaseInfoVOS={}", pkBaseInfoVOS.toString());
    }

    @Test
    public void selectPersonPkInfoByStudentId() {
        List<Map<String, Object>> maps = pkCopyBaseMapper.selectPersonPkInfoByStudentId(7846L, DateUtil.getDateOfWeekDay(1));
        log.info("maps={}", maps);
    }
}
