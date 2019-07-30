package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.MemoryCapacity;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zdjy
 * @since 2019-07-29
 */
public interface MemoryCapacityService extends BaseService<MemoryCapacity> {

    ServerResponse<Object> getEnterMemoryCapacity(HttpSession session);

    ServerResponse<Object> saveMemoryCapacity(HttpSession session, Integer grade, Integer fraction);
}
