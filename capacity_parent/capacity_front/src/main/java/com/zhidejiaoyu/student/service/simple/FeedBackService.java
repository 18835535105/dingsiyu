package com.zhidejiaoyu.student.service.simple;

import com.zhidejiaoyu.common.pojo.MessageBoard;
import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;
import com.zhidejiaoyu.student.vo.feedbackvo.FeedBackInfoVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

/**
 * 学生反馈
 *
 * @author wuchenxi
 * @date 2018/8/13
 */
public interface FeedBackService extends BaseService<MessageBoard> {

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
     * @return
     */
    ServerResponse saveFeedBack(HttpSession session, String content);

    /**
     * 提交意见反馈之前校验文字信息并上传文件
     *
     * @param session
     * @param content 反馈内容
     * @param files   上传的图片
     * @return 返回图片在服务器中的地址路径
     */
    ServerResponse checkFeedBack(HttpSession session, String content, MultipartFile[] files);

    /**
     * 学生阅读管理员回复的信息后，将阅读状态置为学生已读状态
     *
     * @param httpSession
     * @return
     */
    ServerResponse<String> cancelRedPoint(HttpSession httpSession);
}
