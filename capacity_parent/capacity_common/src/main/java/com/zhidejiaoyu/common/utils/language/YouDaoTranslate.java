package com.zhidejiaoyu.common.utils.language;

import com.alibaba.csp.ahas.shaded.com.alibaba.fastjson.JSONArray;
import com.alibaba.csp.ahas.shaded.com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.vo.read.WordInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 有道翻译web api
 *
 * @author wuchenxi
 * @date 2018年4月23日 下午6:47:49
 */
@Slf4j
@Component
public class YouDaoTranslate {

    @Value("${youdao.appKey}")
    private String appKey;

    @Value("${youdao.from}")
    private String from;

    @Value("${youdao.to}")
    private String to;

    @Value("${youdao.youdaoUrl}")
    private String youdaoUrl;

    @Value("${youdao.md5Key}")
    private String md5Key;

    @Resource
    private BaiduSpeak baiduSpeak;

    @Resource
    private RestTemplate restTemplate;

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
        String readUrl = baiduSpeak.getLanguagePath(text);

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
        String errorCode = jsonObject.getString("errorCode");
        String successCode = "0";
        if (!Objects.equals(successCode, errorCode)) {
            log.error("请求有道翻译接口出错！错误码=[{}]，响应信息:[{}]", errorCode, jsonObject.toJSONString());
            throw new ServiceException("请求有道翻译接口出错！");
        }

        JSONObject basic = jsonObject.getJSONObject("basic");
        JSONArray translations = basic.getJSONArray("explains");
        // 释义
        List<String> translates = translations.stream().map(object -> {
            if (object == null) {
                return "";
            }
            return object.toString();
        }).collect(Collectors.toList());
        String usPhonetic = basic.getString("us-phonetic");
        String usReadUrl = basic.getString("us-speech");
        String ukPhonetic = basic.getString("uk-phonetic");
        String ukReadUrl = basic.getString("uk-speech");

        WordInfoVo wordInfoVo = new WordInfoVo();
        wordInfoVo.setWord(word);
        wordInfoVo.setTranslates(translates);
        wordInfoVo.setUkPhonetic("[" + ukPhonetic + "]");
        wordInfoVo.setUsPhonetic("[" + usPhonetic + "]");
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
        String result = restTemplate.postForObject(youdaoUrl, params, String.class);
        return JSONObject.parseObject(result);
    }

}
