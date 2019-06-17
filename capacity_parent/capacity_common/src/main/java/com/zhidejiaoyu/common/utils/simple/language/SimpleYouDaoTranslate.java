package com.zhidejiaoyu.common.utils.simple.language;

import com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.utils.simple.http.SimpleHttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 有道翻译web api
 *
 * @author wuchenxi
 * @date 2018年4月23日 下午6:47:49
 */
@Slf4j
@Component
public class SimpleYouDaoTranslate {

    @Value("${appKey}")
    private String appKey;

    @Value("${from}")
    private String from;

    @Value("${to}")
    private String to;

    @Value("${youdaoUrl}")
    private String youdaoUrl;

    @Value("${md5Key}")
    private String md5Key;

    @Autowired
    private SimpleBaiduSpeak simpleBaiduSpeak;

    @Autowired
    private SimpleHttpClientUtil simpleHttpClientUtil;

    /**
     * 获取单词详细信息
     *
     * @param text 需要翻译的单词
     * @return map key:value translation:单词释义 phonetic：单词音标 explains：单词词性
     * readUrl：单词读音地址
     * @throws Exception
     */
    public Map<String, String> getResultMap(String text) throws Exception {
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = new Md5Hash(appKey + text + salt + md5Key).toString();

        Map<String, String> params = new HashMap<>(16);
        params.put("q", text);
        params.put("from", from);
        params.put("to", to);
        params.put("sign", sign);
        params.put("salt", salt);
        params.put("appKey", appKey);
        String result = simpleHttpClientUtil.post(youdaoUrl, params);

        JSONObject jsonObject = JSONObject.parseObject(result);

        // 释义
        String translation = "";
        try {
            translation = jsonObject.getJSONArray("translation").getString(0);
        } catch (Exception e) {
            log.error("单词[{}]获取释义失败", text);
        }
        // 音标
        String phonetic = "";
        try {
            phonetic = jsonObject.getJSONObject("basic").getString("us-phonetic");
        } catch (Exception ignored) {
        }
        // 词性
        String explains = "";
        try {
            explains = jsonObject.getJSONObject("basic").getJSONArray("explains").toString();
        } catch (Exception ignored) {
        }
        // 读音地址
        String readUrl = simpleBaiduSpeak.getLanguagePath(text);

        Map<String, String> map = new HashMap<>(16);
        map.put("translation", translation);
        map.put("phonetic", phonetic);
        map.put("explains", explains);
        map.put("readUrl", readUrl);
        return map;
    }

}