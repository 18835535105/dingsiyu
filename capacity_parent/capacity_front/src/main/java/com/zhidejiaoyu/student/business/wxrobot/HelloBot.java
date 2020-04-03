package com.zhidejiaoyu.student.business.wxrobot;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.alimt.model.v20181012.TranslateECommerceRequest;
import com.aliyuncs.alimt.model.v20181012.TranslateECommerceResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.zhidejiaoyu.aliyuncommon.constant.AliyunInfoConstant;
import io.github.biezhi.wechat.WeChatBot;
import io.github.biezhi.wechat.api.annotation.Bind;
import io.github.biezhi.wechat.api.constant.Config;
import io.github.biezhi.wechat.api.enums.MsgType;
import io.github.biezhi.wechat.api.model.WeChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * @author: wuchenxi
 * @date: 2020/4/2 13:28:28
 */
@Slf4j
public class HelloBot extends WeChatBot {

    private static final Pattern COMPILE = Pattern.compile("^[A-Za-z]+$");

    public HelloBot(Config config) {
        super(config);
    }

    public static String zhToEn(String text) {

        String regionId = "cn-hangzhou";
        DefaultProfile profile = DefaultProfile.getProfile(regionId, AliyunInfoConstant.accessKeyId, AliyunInfoConstant.accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);

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
            int success = 200;
            if (acsResponse.getCode() == success) {
                return acsResponse.getData().getTranslated();
            }
            log.warn("机器翻译未能正确翻译，错误信息{}", acsResponse.toString());
        } catch (UnsupportedEncodingException | ClientException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Bind(msgType = MsgType.TEXT)
    public void handleText(WeChatMessage message) throws UnsupportedEncodingException {
        log.info(JSONObject.toJSONString(message));

//        群名称/用户昵称
        message.getName();

        if (StringUtils.isNotEmpty(message.getName())) {
            if (message.isAtMe()) {
                String text = message.getText();
                log.info("接收到 [{}] 的消息: {}", message.getName(), text);

                String replaceText = StringUtils.trim(text).replace(" ", "");
                if (COMPILE.matcher(replaceText).matches()) {
                    // 纯英文读出来
                    String url = "https://dict.youdao.com/dictvoice?type=2&audio=" + text;
                    this.sendMsg(message.getFromUserName(), url);
                } else {
                    // 其他的翻译成英文
                    this.sendMsg(message.getFromUserName(), zhToEn(replaceText));
                }
            }
        }
    }

    public static void main(String[] args) {
        new HelloBot(Config.me().autoLogin(true).showTerminal(true)).start();
    }
}
