package com.zhidejiaoyu.student.business.shipconfig.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;
import com.zhidejiaoyu.student.business.shipconfig.vo.IndexVO;

import java.util.List;
import java.util.Map;

/**
 * 飞船配置首页
 *
 * @author: wuchenxi
 * @date: 2020/2/27 15:29:29
 */
public interface ShipIndexService extends BaseService<Student> {

    /**
     * 首页数据
     *
     * @return
     */
    ServerResponse<Object> index();

    /**
     * 源分战力排行
     *
     * @param type 1：全国排行；2：校区排行
     * @return
     */
    ServerResponse<Object> rank(Integer type);

    /**
     * 获取各项战力值
     * @param equipments
     * @return
     */
    IndexVO.BaseValue getMaxValue(List<Map<String, Object>> equipments);
}
