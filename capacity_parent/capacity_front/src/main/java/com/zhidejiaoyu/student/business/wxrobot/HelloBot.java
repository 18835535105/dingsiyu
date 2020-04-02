package com.zhidejiaoyu.student.business.wxrobot;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import io.github.biezhi.wechat.WeChatBot;
import io.github.biezhi.wechat.api.annotation.Bind;
import io.github.biezhi.wechat.api.constant.Config;
import io.github.biezhi.wechat.api.enums.MsgType;
import io.github.biezhi.wechat.api.model.WeChatMessage;
import io.github.biezhi.wechat.utils.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.RequestBuilder;

import java.io.IOException;
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

    @Bind(msgType = MsgType.TEXT)
    public void handleText(WeChatMessage message) throws IOException {


        log.info(JSONObject.toJSONString(message));
        log.info(message.toString());
//        群名称/用户昵称
        message.getName();


        if (StringUtils.isNotEmpty(message.getName())) {
            if (message.isAtMe()) {
                log.info("接收到 [{}] 的消息: {}", message.getName(), message.getText());

                if (COMPILE.matcher(message.getText()).matches()) {
                    // 纯英文读出来
                    String url = "https://fanyi.baidu.com/gettts?lan=en&spd=3&source=web&text=" + message.getText();
                    OkHttpClient okHttpClient = OkHttpUtils.configureToIgnoreCertificate(new OkHttpClient.Builder()).build();
                    Request request = new Request.Builder().url(url)
                            .get()
                            .build();
                    Call call = okHttpClient.newCall(request);
                    Response response = call.execute();


                } else {
                    // 其他的翻译成英文
                }

                this.sendMsg(message.getFromUserName(), "自动回复: " + message.getText());
            }

        }
    }

    public static void main(String[] args) throws IOException {
//        new HelloBot(Config.me().autoLogin(true).showTerminal(true)).start();

        String url = "https://fanyi.baidu.com/gettts?lan=en&spd=3&source=web&text=hello";
        OkHttpClient okHttpClient = OkHttpUtils.configureToIgnoreCertificate(new OkHttpClient.Builder()).build();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        System.out.println(JSONObject.toJSONString(response.body()));
    }
}
