package com.zhidejiaoyu.student.business.wechat.qy.fly.service.impl;

import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.mapper.CurrentDayOfStudyMapper;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.utils.IdUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.qy.fly.service.QyFlyService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: wuchenxi
 * @date: 2020/6/16 13:54:54
 */
@Service
public class QyFlyServiceImpl implements QyFlyService {

    @Resource
    private CurrentDayOfStudyMapper currentDayOfStudyMapper;

    @Override
    public ServerResponse<Object> uploadFlyRecord(MultipartFile file, Long studentId, Integer num) {

        String dir = FileConstant.STUDENT_FLY_RECORD + DateUtil.formatYYYYMMDD(new Date()) + "/";
        String upload = OssUpload.upload(file, dir, IdUtil.getId());

        currentDayOfStudyMapper.insert(CurrentDayOfStudy.builder()
                .createTime(new Date())
                .imgUrl(upload)
                .qrCodeNum(num)
                .studentId(studentId)
                .build());


        return ServerResponse.createBySuccess();
    }
}
