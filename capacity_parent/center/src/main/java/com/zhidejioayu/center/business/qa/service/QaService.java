package com.zhidejioayu.center.business.qa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.center.QaQuestion;
import org.springframework.web.multipart.MultipartFile;

public interface QaService extends IService<QaQuestion> {
    void importQa(MultipartFile file);
}
