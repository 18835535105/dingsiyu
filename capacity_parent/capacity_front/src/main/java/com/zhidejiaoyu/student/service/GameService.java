package com.zhidejiaoyu.student.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhidejiaoyu.common.Vo.game.GameOneVo;
import com.zhidejiaoyu.common.pojo.GameStore;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @author wuchenxi
 * @date 2018/10/29
 */
public interface GameService extends BaseService<GameStore> {

    /**
     * 获取游戏《冰火两重界》试题
     *
     * @param session
     * @return
     */
    ServerResponse<GameOneVo> getGameOne(HttpSession session);
}
