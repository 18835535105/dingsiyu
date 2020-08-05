package com.zhidejiaoyu.student.business.shipconfig.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;
import com.zhidejiaoyu.student.business.shipconfig.dto.ShipConfigInfoDTO;
import com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.IndexVO;

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
    ServerResponse<IndexVO> index();

    /**
     * 源分战力排行
     *
     * @param type 1：全国排行；2：校区排行;3：同服务器排行
     * @return
     */
    ServerResponse<Object> rank(Integer type);

    /**
     * 获取学生雷达图数据
     *
     * @return
     * @param openId
     */
    IndexVO.MyState getBaseState(String openId);

    /**
     * 获取各项战力值
     *
     * @param equipments
     * @return
     */
    IndexVO.BaseValue getBaseValue(List<Map<String, Object>> equipments);

    /**
     * 初始化源分战力排行
     */
    void initRank();

    /**
     * 获取最近7天状态
     *
     * @param studentId
     * @return
     */
    IndexVO.BaseValue getStateOfWeek(Long studentId);

    /**
     * 保存学生选择的勋章
     *
     * @param medalId id之间用英文,隔开
     * @return
     */
    ServerResponse<Object> saveMedal(String medalId);

    ShipConfigInfoDTO getShipConfigInfoDTO(Map<String, Object> map);

    /**
     * 查看其他人的飞船配置页面
     *
     * @param studentId
     * @return
     */
    ServerResponse<Object> otherIndex(Long studentId);

    /**
     * 获取学生勋章状态，用于学生选择勋章
     *
     * @return
     */
    ServerResponse<Object> getMedalStatus();
}
