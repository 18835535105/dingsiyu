package com.zhidejiaoyu.student.business.wechat.qy.fly.service;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: wuchenxi
 * @date: 2020/6/16 13:54:54
 */
public interface QyFlyService {

    /**
     * 上传学生飞行记录
     *
     * @param file
     * @param studentId
     * @param num       作业本中二维码序号
     * @return
     */
    ServerResponse<Object> uploadFlyRecord(MultipartFile file, Long studentId, Integer num);
}
