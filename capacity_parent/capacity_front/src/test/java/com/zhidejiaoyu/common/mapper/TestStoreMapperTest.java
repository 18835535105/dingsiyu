package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.vo.goldtestvo.GoldTestVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/4/20 16:50:50
 */
@Slf4j
public class TestStoreMapperTest extends BaseTest {

    @Resource
    private TestStoreMapper testStoreMapper;

    @Test
    public void testSelectSubjectsByUnitId() {
        List<GoldTestVO> goldTestVOS = testStoreMapper.selectSubjectsByUnitId(101031L);
        log.info("vo={}", goldTestVOS);
    }
}
