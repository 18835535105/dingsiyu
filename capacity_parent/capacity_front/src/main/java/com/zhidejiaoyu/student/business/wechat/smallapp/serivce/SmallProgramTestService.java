package com.zhidejiaoyu.student.business.wechat.smallapp.serivce;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.business.service.BaseService;

import javax.servlet.http.HttpSession;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:01:01
 */
public interface SmallProgramTestService extends BaseService<Student> {


    Object getTest(HttpSession session, String openId);

    Object saveTest(Integer point, HttpSession session, String openId);

    Object getQRCode(String openId, String weChatName, String weChatImgUrl);
}
