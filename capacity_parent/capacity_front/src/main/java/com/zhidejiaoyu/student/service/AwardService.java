package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.Award;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.vo.AwardVo;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 任务奖励相关service
 */
public interface AwardService extends BaseService<Award> {

    /**
     * 获取学生任务奖励信息
     *
     * @param session
     * @param type 1：日奖励；2：任务奖励；3：勋章
     * @return
     */
    ServerResponse<List<AwardVo>> getAwareInfo(HttpSession session, Integer type);

    /**
     * 领取奖励
     *
     * @param session
     * @param awareId 奖励id
     * @param type 领取类型：1：领取金币将励；2：领取勋章奖励
     * @return
     */
    ServerResponse<String> getAware(HttpSession session, Long awareId, Integer type);
}
