package com.zhidejioayu.center.business.wechat.smallapp.serivce.impl;

import com.zhidejiaoyu.common.constant.GradeNameConstant;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.center.BusinessUserInfoMapper;
import com.zhidejiaoyu.common.mapper.center.StudentWechatVideoMapper;
import com.zhidejiaoyu.common.mapper.center.WechatVideoMapper;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejiaoyu.common.pojo.center.StudentWechatVideo;
import com.zhidejiaoyu.common.pojo.center.WechatVideo;
import com.zhidejiaoyu.common.utils.IdUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.wechat.smallapp.video.VideoVO;
import com.zhidejioayu.center.business.feignclient.student.BaseStudentFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/4/15 16:53:53
 */
@Service
public class VideoServiceImpl implements VideoService {


    @Resource
    private StudentWechatVideoMapper studentWechatVideoMapper;

    @Resource
    private BusinessUserInfoMapper businessUserInfoMapper;

    @Resource
    private WechatVideoMapper wechatVideoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> saveVideo(String openId, Integer gold, String videoId) {

        BusinessUserInfo businessUserInfo = businessUserInfoMapper.selectStudentInfoByOpenId(openId);

        BaseStudentFeignClient baseStudentFeignClient = FeignClientUtil.getBaseStudentFeignClientByUuid(businessUserInfo.getUserUuid());
        baseStudentFeignClient.saveGold(openId, gold);

        studentWechatVideoMapper.insert(StudentWechatVideo.builder()
                .studentUuid(businessUserInfo.getUserUuid())
                .createTime(new Date())
                .id(IdUtil.getId())
                .wechatVideoId(videoId)
                .build());

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Object> getVideo(String openId) {
        BusinessUserInfo businessUserInfo = businessUserInfoMapper.selectStudentInfoByOpenId(openId);
        if (businessUserInfo == null) {
            throw new ServiceException("中台服务未查询到openid= " + openId + " 的学生信息！");
        }
        String userUuid = businessUserInfo.getUserUuid();
        BaseStudentFeignClient baseStudentFeignClientByUuid = FeignClientUtil.getBaseStudentFeignClientByUuid(userUuid);
        String grade = baseStudentFeignClientByUuid.getStudentGradeByOpenId(openId);

        Map<String, String> map = new HashMap<>(16);
        map.put(GradeNameConstant.THREE_GRADE, GradeNameConstant.THREE_GRADE);
        map.put(GradeNameConstant.FOURTH_GRADE, GradeNameConstant.FOURTH_GRADE);
        map.put(GradeNameConstant.FIFTH_GRADE, GradeNameConstant.FIFTH_GRADE);
        map.put(GradeNameConstant.SIXTH_GRADE, GradeNameConstant.SIXTH_GRADE);

        if (!map.containsKey(grade)) {
            return ServerResponse.createByError(401, "暂未当前年级视频！");
        }

        WechatVideo wechatVideo = wechatVideoMapper.selectNextVideo(userUuid, grade);
        if (wechatVideo != null) {
            return ServerResponse.createBySuccess(VideoVO.builder()
                    .id(wechatVideo.getId())
                    .url(wechatVideo.getUrl())
                    .build());
        }

        studentWechatVideoMapper.deleteByStudentUuid(userUuid);
        wechatVideo = wechatVideoMapper.selectNextVideo(userUuid, grade);
        return ServerResponse.createBySuccess(VideoVO.builder()
                .id(wechatVideo.getId())
                .url(wechatVideo.getUrl())
                .build());
    }
}
