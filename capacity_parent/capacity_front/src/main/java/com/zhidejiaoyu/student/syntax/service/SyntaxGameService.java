package com.zhidejiaoyu.student.syntax.service;

import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.BaseService;

/**
 * 语法游戏
 *
 * @author: wuchenxi
 * @Date: 2019/10/29 17:13
 */
public interface SyntaxGameService extends BaseService<SyntaxTopic> {

    /**
     * 获取超级语法小游戏数据
     *
     * @param unitId    超级语法单元id
     * @return
     */
    ServerResponse getSyntaxGame(Long unitId);

    /**
     * 保存超级语法小游戏数据
     *
     * @param testRecord
     * @return
     */
    ServerResponse saveSyntaxGame(TestRecord testRecord);
}
