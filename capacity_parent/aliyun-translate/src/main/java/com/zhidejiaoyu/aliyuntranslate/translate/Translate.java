package com.zhidejiaoyu.aliyuntranslate.translate;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.alimt.model.v20181012.TranslateECommerceRequest;
import com.aliyuncs.alimt.model.v20181012.TranslateECommerceResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author: wuchenxi
 * @date: 2020/4/3 09:28:28
 */
@Slf4j
@Component
public class Translate {

    private static final int SUCCESS = 200;

    private static DefaultAcsClient client;

    @Resource(name = "translateClient")
    private DefaultAcsClient defaultAcsClient;

    @PostConstruct
    public void init() {
        client = this.defaultAcsClient;
    }

    /**
     * 中文翻译成英文
     *
     * @param text 需要翻译的内容
     * @return 翻译的结果
     */
    public static String zhToEn(String text) {
        try {
            TranslateECommerceRequest eCommerceRequest = new TranslateECommerceRequest();
            eCommerceRequest.setScene("title");
            // 设置请求方式，POST
            eCommerceRequest.setMethod(MethodType.POST);
            //翻译文本的格式
            eCommerceRequest.setFormatType("text");
            //源语言
            eCommerceRequest.setSourceLanguage("zh");
            //原文
            eCommerceRequest.setSourceText(URLEncoder.encode(text, "UTF-8"));
            //目标语言
            eCommerceRequest.setTargetLanguage("en");

            TranslateECommerceResponse acsResponse = client.getAcsResponse(eCommerceRequest);
            if (acsResponse.getCode() == SUCCESS) {
                return acsResponse.getData().getTranslated();
            }
            log.warn("机器翻译未能正确翻译，错误信息{}", acsResponse.toString());
        } catch (UnsupportedEncodingException | ClientException e) {
            e.printStackTrace();
        }
        return "";
    }
}
