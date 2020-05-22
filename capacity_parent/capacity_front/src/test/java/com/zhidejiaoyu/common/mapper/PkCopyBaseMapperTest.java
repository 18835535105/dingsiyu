package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.ZdjyFrontApplication;
import com.zhidejiaoyu.common.vo.ship.SchoolPkBaseInfoVO;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

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
        List<SchoolPkBaseInfoVO> schoolPkBaseInfoVOS = pkCopyBaseMapper.selectSchoolPkBaseInfoByType(2, 100);
        log.info("schoolPkBaseInfoVOS={}", schoolPkBaseInfoVOS.toString());
    }
}
