package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author wuchenxi
 * @date 2019-03-13
 */

public class CcieMapperTest extends BaseTest {

    @Autowired
    private CcieMapper ccieMapper;

    @Test
    public void test() {
        String n20190313 = ccieMapper.selectMaxCcieNo(1, "N20190313");
        System.out.println(n20190313);
    }
}