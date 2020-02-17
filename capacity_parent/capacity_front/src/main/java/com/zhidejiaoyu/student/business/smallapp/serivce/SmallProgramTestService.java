package com.zhidejiaoyu.student.business.smallapp.serivce;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;
import com.zhidejiaoyu.student.business.smallapp.dto.BindAccountDTO;

import javax.servlet.http.HttpSession;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:01:01
 */
public interface SmallProgramTestService extends BaseService<Student> {


    Object getTest(HttpSession session);
}
