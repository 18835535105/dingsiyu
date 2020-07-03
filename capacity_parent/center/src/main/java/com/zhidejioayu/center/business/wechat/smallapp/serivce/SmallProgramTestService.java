package com.zhidejioayu.center.business.wechat.smallapp.serivce;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.Student;

import javax.servlet.http.HttpSession;

/**
 * 首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:01:01
 */
public interface SmallProgramTestService extends IService<Student> {


    Object getTest(HttpSession session, String openId);

    Object saveTest(Integer point, HttpSession session, String openId);

    Object getQRCode(String openId, String weChatName, String weChatImgUrl);
}
