package com.zhidejioayu.center.business.wechat.qy.fly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.mapper.CurrentDayOfStudyMapper;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.utils.IdUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.util.BaseFeignClientUtil;
import com.zhidejioayu.center.business.wechat.feignclient.qy.BaseQyFeignClient;
import com.zhidejioayu.center.business.wechat.qy.fly.service.QyFlyService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/6/16 13:54:54
 */
@Service
public class QyFlyServiceImpl extends ServiceImpl<CurrentDayOfStudyMapper, CurrentDayOfStudy> implements QyFlyService {

    @Resource
    private Map<String, BaseQyFeignClient> qyServerFeignClient;

    @Override
    public ServerResponse<Object> getCurrentDayOfStudy(String openId) {
        BaseQyFeignClient baseQyFeignClient = BaseFeignClientUtil.getBaseQyFeignClient(openId);
        return baseQyFeignClient.getCurrentDayOfStudy(openId);
    }

    @Override
    public ServerResponse<Object> uploadFlyRecord(MultipartFile file, String openId, Integer num, Long studentId) {
        BaseQyFeignClient baseQyFeignClient = BaseFeignClientUtil.getBaseQyFeignClient(openId);

        boolean flag = baseQyFeignClient.checkUpload(openId);
        if (!flag) {
            return ServerResponse.createByError(400, "学生当天信息已经上传，不能再次上传！");
        }

        Date date = new Date();
        String dir = FileConstant.STUDENT_FLY_RECORD + DateUtil.formatYYYYMMDD(date) + "/";

        String upload = OssUpload.upload(file, dir, IdUtil.getId());

        baseQyFeignClient.saveCurrentDayOfStudy(CurrentDayOfStudy.builder()
                .createTime(date)
                .imgUrl(upload)
                .qrCodeNum(num)
                .studentId(studentId)
                .build());

        return ServerResponse.createBySuccess();
    }
}
