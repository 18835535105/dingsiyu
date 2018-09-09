package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.vo.feedbackvo.FeedBackInfoVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

/**
 * 学生反馈
 *
 * @author wuchenxi
 * @date 2018/8/13
 */
public interface FeedBackService {

    /**
     * 学生发起意见反馈页面数据获取
     *
     * @param session
     * @return
     */
    ServerResponse<FeedBackInfoVO> getFeedBacks(HttpSession session);

    ServerResponse cancelHint(HttpSession session);

    /**
     * 保存意见反馈
     *
     * @param session
     * @param content 反馈内容
     * @param files   上传的图片
     * @return
     */
    ServerResponse saveFeedBack(HttpSession session, String content, MultipartFile[] files);
}
