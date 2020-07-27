package com.zhidejioayu.center.business.wechat.qy.qa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.center.QaQuestion;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

public interface QaService extends IService<QaQuestion> {
    void importQa(MultipartFile file);

    /**
     * 根据问题获取答案
     *
     * @param question
     * @return
     */
    ServerResponse<Object> getQa(String question);

    /**
     * 保存自学习
     *
     * @param question
     * @param questionId
     * @return
     */
    void saveQaAutoStudy(String question, Long questionId);

    /**
     * 保存未知问题
     *
     * @param question
     */
    void saveUnknownQuestion(String question);

    /**
     * 获取其他答案
     *
     * @param question
     * @return
     */
    ServerResponse<Object> getQaOtherAnswer(String question);
}
