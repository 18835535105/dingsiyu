package com.zhidejiaoyu.student.business.test.service;

import com.zhidejiaoyu.common.pojo.TestStore;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;
import com.zhidejiaoyu.student.business.test.dto.SaveGoldTestDTO;

/**
 * 金币试卷
 *
 * @author: wuchenxi
 * @date: 2020/4/17 09:09:09
 */
public interface GoldTestService extends BaseService<TestStore> {

    /**
     * 获取金币试卷试题
     *
     * @param unitId
     * @return
     */
    ServerResponse<Object> getTest(Long unitId);

    /**
     * 保存测试记录
     *
     * @param dto
     * @return
     */
    ServerResponse<Object> saveTest(SaveGoldTestDTO dto);
}
