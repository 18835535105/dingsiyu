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
}
