package com.zhidejiaoyu.student.controller.simple;

import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.SimpleFeedBackServiceSimple;
import com.zhidejiaoyu.student.vo.feedbackvo.FeedBackInfoVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

/**
 * 意见反馈
 *
 * @author wuchenxi
 * @date 2018/8/13
 */
@RestController
@RequestMapping("/api/feedBack")
public class SimpleFeedBackController {

    @Autowired
    private SimpleFeedBackServiceSimple feedBackService;

    /**
     * 学生发起意见反馈页面数据获取
     * 获取内容有：
     * <ul>
     *     <li>学生是否因反馈被采纳而获取金币，金币数</li>
     *     <li>学生头像</li>
     *     <li>管理人员头像</li>
     *     <li>学生反馈内容</li>
     *     <li>管理人员回复内容</li>
     *     <li>反馈时间</li>
     *     <li>回复时间</li>
     * </ul>
     */
    @GetMapping("/getFeedBacks")
    public ServerResponse<FeedBackInfoVO> getFeedBacks(HttpSession session){
        return feedBackService.getFeedBacks(session);
    }

    /**
     * 当次金币奖励信息提示之后，将显示标识置为不显示状态
     */
    @PostMapping("/cancelHint")
    public ServerResponse cancelHint(HttpSession session) {
        return feedBackService.cancelHint(session);
    }

    /**
     * 保存意见反馈
     * <br>
     * <ul>
     * <li>如果参数中有图片信息，先验证 <code>content</code> 中是否含有敏感词信息，如果含有敏感词信息，返回响应的响应信息并不上传图片；
     * 如果不含有敏感词信息，上传图片，并返回图片在服务器中的路径信息</li>
     * <li>如果参数中不含有图片信息，保存<code>content</code>内容</li>
     * </ul>
     *
     * @param session
     * @param content 反馈内容
     * @return
     */
    @PostMapping("/saveFeedBack")
    public ServerResponse saveFeedBack(HttpSession session, String content) {
        if (StringUtils.isEmpty(content)) {
            return ServerResponse.createByErrorMessage("反馈内容不能为空！");
        }
        return feedBackService.saveFeedBack(session, content);
    }

    /**
     * 提交意见反馈之前校验文字信息并上传文件
     *
     * @param session
     * @param content 反馈内容
     * @param files   上传的图片
     * @return 返回图片在服务器中的地址路径
     */
    @PostMapping("/checkFeedBack")
    public ServerResponse checkFeedBack(HttpSession session, String content, @RequestParam(required = false) MultipartFile[] files) {
        // 上传图片不能超过20张
        if (files != null && files.length > 20) {
            return ServerResponse.createByErrorMessage("反馈图片不能超过20张，请修改后重新提交！");
        }
        if (StringUtils.isEmpty(content)) {
            return ServerResponse.createByErrorMessage("反馈内容不能为空！");
        }
        // 反馈文字不能超过200个
        if (content.length() > 200) {
            return ServerResponse.createByErrorMessage("输入文字过长！");
        }
        return feedBackService.checkFeedBack(session, content, files);
    }

    /**
     * 学生阅读管理员回复的信息后，将阅读状态置为学生已读状态
     *
     * @param httpSession
     * @return
     */
    @PostMapping("/cancelRedPoint")
    public ServerResponse<String> cancelRedPoint(HttpSession httpSession) {
        return feedBackService.cancelRedPoint(httpSession);
    }
}
