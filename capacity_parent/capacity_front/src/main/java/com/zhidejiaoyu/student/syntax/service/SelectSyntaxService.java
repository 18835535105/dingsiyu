package com.zhidejiaoyu.student.syntax.service;

import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.BaseService;

/**
 * @author: wuchenxi
 * @Date: 2019/10/31 17:51
 */
public interface SelectSyntaxService extends BaseService<SyntaxTopic> {

    /**
     * 获取选语法数据
     *
     * @param unitId
     * @return
     */
    ServerResponse getSelectSyntax(Long unitId);

    /**
     * 保存写语法数据
     *
     * @param learn
     * @param known
     * @return
     */
    ServerResponse saveSelectSyntax(Learn learn, Boolean known);
}
