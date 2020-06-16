package com.zhidejiaoyu.student.business.wechat.qy.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.dto.wechat.qy.fly.SearchStudentDTO;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: wuchenxi
 * @date: 2020/6/16 13:54:54
 */
public interface QyFlyService extends IService<CurrentDayOfStudy> {

    /**
     * 上传学生飞行记录
     *
     * @param file
     * @param studentId
     * @param num       作业本中二维码序号
     * @return
     */
    ServerResponse<Object> uploadFlyRecord(MultipartFile file, Long studentId, Integer num);

    /**
     * 获取当前教师下的所有学生
     *
     * @param openId    教师openId
     * @param dto 查询条件
     * @return
     */
    ServerResponse<Object> getStudents(String openId, SearchStudentDTO dto);
}
