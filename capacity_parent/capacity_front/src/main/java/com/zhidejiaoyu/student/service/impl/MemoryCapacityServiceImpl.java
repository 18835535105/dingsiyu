package com.zhidejiaoyu.student.service.impl;

import com.zhidejiaoyu.common.mapper.MemoryCapacityMapper;
import com.zhidejiaoyu.common.pojo.MemoryCapacity;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.MemoryCapacityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2019-07-29
 */
@Service
public class MemoryCapacityServiceImpl extends BaseServiceImpl<MemoryCapacityMapper, MemoryCapacity> implements MemoryCapacityService {

    @Autowired
    private MemoryCapacityMapper memoryCapacityMapper;

    @Override
    public ServerResponse<Object> getEnterMemoryCapacity(HttpSession session) {
        Long studentId = getStudentId(session);
        Integer count = memoryCapacityMapper.selTodayMemoryCapacity(studentId);
        Map<String, Object> map = new HashMap<>();
        if (count == null || count.equals(0)) {
            map.put("isEnter", true);
        } else {
            map.put("isEnter", false);
        }
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse<Object> saveMemoryCapacity(HttpSession session, Integer grade, Integer fraction) {
        //判断是否为当日第一次保存
        Long studentId = getStudentId(session);
        Integer count = memoryCapacityMapper.selTodayMemoryCapacity(studentId);
        Map<String, Object> map = new HashMap<>();
        Integer gold = 0;
        String url = null;
        if (count == null || count.equals(0)) {
            if (fraction >= 80) {
                switch (grade){
                    case 1:
                        gold=1;
                        break;
                    case 2:
                        gold=1;
                        break;
                    case 3:
                        gold=1;
                        break;
                    case 4:
                        gold=1;
                        break;
                    case 5:
                        gold=1;
                        break;
                }

            }
        }
        map.put("gold", gold);
        map.put("listen", url);
        return null;
    }
}
