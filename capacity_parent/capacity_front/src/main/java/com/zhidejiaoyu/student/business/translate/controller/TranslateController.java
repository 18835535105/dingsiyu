package com.zhidejiaoyu.student.business.translate.controller;

import com.zhidejiaoyu.aliyuntranslate.translate.Translate;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/4/7 10:24:24
 */
@RestController
@RequestMapping("/translate")
public class TranslateController {

    /**
     * 将中文翻译成英文
     *
     * @param text
     * @return
     */
    @GetMapping("/zhToEn")
    public ServerResponse<Object> zhToEn(String text) {
        if (StringUtils.isEmpty(text)) {
            return ServerResponse.createByError(400, "text can't be null!");
        }
        String s = Translate.zhToEn(text);
        if (StringUtils.isEmpty(s)) {
            return ServerResponse.createByError();
        }

        Map<String, String> map = new HashMap<>(16);
        map.put("word", s);
        return ServerResponse.createBySuccess(map);
    }
}
