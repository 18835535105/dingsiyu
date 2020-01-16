package com.zhidejiaoyu.student.business.syntax.service;

import com.zhidejiaoyu.common.dto.syntax.SaveSyntaxDTO;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;

/**
 * @author: wuchenxi
 * @Date: 2019/10/31 17:42
 */
public interface LearnSyntaxService extends BaseService<SyntaxTopic> {

    /**
     * 获取当前单元的语法数据
     *
     * @param unitId
     * @return
     */
    ServerResponse<Object> getLearnSyntax(Long unitId);

    /**
     * 保存语法数据
     *
     * @param dto
     * @return
     */
    ServerResponse<Object> saveLearnSyntax(SaveSyntaxDTO dto);
}
