package com.zhidejiaoyu.common.utils.simple.language;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.Vo.read.WordInfoVo;
import com.zhidejiaoyu.common.utils.simple.http.SimpleHttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        JSONObject jsonObject = getJsonObject(text);

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

    /**
     * 获取指定单词的信息
     *
     * @param word
     * @return
     */
    public WordInfoVo getWordInfoVo(String word) {
        if (StringUtils.isEmpty(word)) {
            return null;
        }
        word = word.trim();
        JSONObject jsonObject = this.getJsonObject(word);

        if (jsonObject == null) {
            return null;
        }

        JSONArray translations = jsonObject.getJSONArray("translation");
        // 释义
        List<String> translates = translations.stream().map(object -> {
            if (object == null) {
                return "";
            }
           return jsonObject.toString();
        }).collect(Collectors.toList());
        String usPhonetic = jsonObject.getJSONObject("basic").getString("us-phonetic");
        String usReadUrl = jsonObject.getJSONObject("basic").getString("us-speech");
        String ukPhonetic = jsonObject.getJSONObject("basic").getString("uk-phonetic");
        String ukReadUrl = jsonObject.getJSONObject("basic").getString("uk-speech");

        WordInfoVo wordInfoVo = new WordInfoVo();
        wordInfoVo.setWord(word);
        wordInfoVo.setTranslates(translates);
        wordInfoVo.setUkPhonetic(ukPhonetic);
        wordInfoVo.setUsPhonetic(usPhonetic);
        wordInfoVo.setUsReadUrl(usReadUrl);
        wordInfoVo.setUkReadUrl(ukReadUrl);

        return wordInfoVo;
    }

    /**
     * 获取单词翻译响应数据
     *
     * @param text
     * @return
     * @throws IOException
     */
    private JSONObject getJsonObject(String text) {
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = new Md5Hash(appKey + text + salt + md5Key).toString();

        Map<String, String> params = new HashMap<>(16);
        params.put("q", text);
        params.put("from", from);
        params.put("to", to);
        params.put("sign", sign);
        params.put("salt", salt);
        params.put("appKey", appKey);
        String result = null;
        try {
            result = simpleHttpClientUtil.post(youdaoUrl, params);
        } catch (IOException e) {
            log.error("调用有道翻译接口获取单词翻译信息出错！word=[{}]", text, e);
        }
        return JSONObject.parseObject(result);
    }

}
