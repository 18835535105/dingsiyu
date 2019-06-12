package com.zhidejiaoyu.student.service.simple;

import com.zhidejiaoyu.common.Vo.simple.AwardVo;
import com.zhidejiaoyu.common.pojo.Award;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 任务奖励相关service
 */
public interface SimpleAwardServiceSimple extends SimpleBaseService<Award> {

    /**
     * 获取学生任务奖励信息
     *
     * @param session
     * @param type    1：日奖励；2：任务奖励；3：勋章
     * @return
     */
    ServerResponse<List<AwardVo>> getAwareInfo(HttpSession session, Integer type);

    /**
     * 领取奖励
     *
     * @param session
     * @param awareId 奖励id
     * @param type    领取类型：1：领取金币将励；2：领取勋章奖励
     * @return
     */
    ServerResponse<String> getAware(HttpSession session, Long awareId, Integer type);


    /**
     * 查看领取奖励数量
     *
     * @return
     */
    ServerResponse<Map<String, Object>> getAwareSize(int type, HttpSession session, Integer model);
}
