package com.zhidejioayu.center.business.wechat.qy.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: wuchenxi
 * @date: 2020/6/16 13:54:54
 */
public interface QyFlyService extends IService<CurrentDayOfStudy> {

    ServerResponse<Object> getCurrentDayOfStudy(String openId);

    /**
     * 上传学生智慧飞行记录
     *
     * @param file
     * @param studentUuid
     * @param num
     * @param studentId
     * @return
     */
    ServerResponse<Object> uploadFlyRecord(MultipartFile file, String studentUuid, Integer num, Long studentId);
}
