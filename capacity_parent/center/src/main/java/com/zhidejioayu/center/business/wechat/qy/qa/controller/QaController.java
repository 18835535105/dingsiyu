package com.zhidejioayu.center.business.wechat.qy.qa.controller;

import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.qy.qa.service.QaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 企业微信李糖心Q&A
 */
@RestController
@RequestMapping("/wechat/qy/qa")
public class QaController {

    @Resource
    private QaService qaService;

    /**
     * 导入李糖心Q&A数据
     *
     * @param file
     * @return
     */
    @RequestMapping("/importQa")
    public Object importQa(MultipartFile file) {
        qaService.importQa(file);
        return 200;
    }

    /**
     * 根据问题获取答案
     *
     * @param question
     * @return
     */
    @GetMapping("/getQa")
    public ServerResponse<Object> getQa(String question) {
        if (StringUtil.isEmpty(question)) {
            return ServerResponse.createByError(400, "question can't be null!");
        }
        return qaService.getQa(StringUtil.trim(question));
    }

    /**
     * 获取其他答案
     *
     * @param question
     * @return
     */
    @GetMapping("/getQaOtherAnswer")
    public ServerResponse<Object> getQaOtherAnswer(String question) {
        if (StringUtil.isEmpty(question)) {
            return ServerResponse.createByError(400, "question can't be null!");
        }
        return qaService.getQaOtherAnswer(StringUtil.trim(question));
    }

    /**
     * 保存自学习
     *
     * @param question
     * @param questionId
     * @return
     */
    @PostMapping("/saveQaAutoStudy")
    public ServerResponse<Object> saveQaAutoStudy(String question, Long questionId) {
        if (StringUtil.isEmpty(question)) {
            return ServerResponse.createByError(400, "question can't be null!");
        }
        if (questionId == null) {
            return ServerResponse.createByError(400, "questionId can't be null!");
        }
        qaService.saveQaAutoStudy(question, questionId);
        return ServerResponse.createBySuccess();
    }

    /**
     * 保存未知问题
     *
     * @return
     */
    @PostMapping("/saveUnknownQuestion")
    public ServerResponse<Object> saveUnknownQuestion(String question) {
        if (StringUtil.isEmpty(question)) {
            return ServerResponse.createByError(400, "question can't be null!");
        }
        qaService.saveUnknownQuestion(question);
        return ServerResponse.createBySuccess();
    }


}
