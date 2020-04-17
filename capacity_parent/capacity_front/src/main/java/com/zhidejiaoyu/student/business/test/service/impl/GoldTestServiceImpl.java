package com.zhidejiaoyu.student.business.test.service.impl;

import com.zhidejiaoyu.common.mapper.TestStoreMapper;
import com.zhidejiaoyu.common.mapper.UnitTestStoreMapper;
import com.zhidejiaoyu.common.pojo.TestStore;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.test.dto.SaveGoldTestDTO;
import com.zhidejiaoyu.student.business.test.service.GoldTestService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/4/17 09:10:10
 */
@Service
public class GoldTestServiceImpl extends BaseServiceImpl<TestStoreMapper, TestStore> implements GoldTestService {

    @Resource
    private UnitTestStoreMapper unitTestStoreMapper;

    @Override
    public ServerResponse<Object> getTest(Long unitId) {
        return null;
    }

    @Override
    public ServerResponse<Object> saveTest(SaveGoldTestDTO dto) {
        return null;
    }
}
