package com.zhidejiaoyu.student.business.smallapp.serivce;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;
import com.zhidejiaoyu.student.business.smallapp.dto.BindAccountDTO;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:01:01
 */
public interface IndexService extends BaseService<Student> {

    /**
     * 绑定队长账号
     *
     * @param dto
     * @return
     */
    ServerResponse<Object> bind(BindAccountDTO dto);
}
