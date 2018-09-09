package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.FeedBackService;
import com.zhidejiaoyu.student.vo.feedbackvo.FeedBackInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * 意见反馈
 *
 * @author wuchenxi
 * @date 2018/8/13
 */
@Validated
@RestController
@RequestMapping("/feedBack")
public class FeedBackController {

    @Autowired
    private FeedBackService feedBackService;

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
     * @param files   上传的图片
     * @return
     */
    @PostMapping("/saveFeedBack")
    public ServerResponse saveFeedBack(HttpSession session, @NotEmpty String content,
                                       @Size(max = 20) @RequestParam(required = false) MultipartFile[] files) {
        // 上传图片不能超过20张
        if (files != null && files.length > 20) {
            return ServerResponse.createByErrorMessage("反馈图片不能超过20张，请修改后重新提交！");
        }
        return feedBackService.saveFeedBack(session, content, files);
    }
}
