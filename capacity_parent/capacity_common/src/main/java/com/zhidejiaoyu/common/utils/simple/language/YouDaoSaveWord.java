package com.zhidejiaoyu.common.utils.simple.language;

import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.utils.simple.http.FtpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 有道在线语音合成web api 保存单词读音
 *
 * @author wuchenxi
 * @date 2018年4月23日 下午6:39:36
 */
@Component
@Slf4j
public class YouDaoSaveWord {

    @Autowired
    private FtpUtil ftpUtil;

    @Value("${youdao.create.url}")
    private String url;

    @Value(("${youdao.create.appKey}"))
    private String appKey;

    @Value("${youdao.create.md5Key}")
    private String md5Key;

    @Value(("${youdao.create.salt}"))
    private String salt;

    @Value("${ftp.prefix}")
    private String prefix;

    private static Map<String, String> wordMap;

    static {
        wordMap = new HashMap<>(16);
        wordMap.put("Mr.", "Mr.");
        wordMap.put("car", "car");
        wordMap.put("before", "before");
    }

    /**
     * 获取语音合成地址
     *
     * @param text 需要合成的文字内容
     * @return
     */
    public String getLanguagePath(String text) {

        return saveWordAudio(text);

        // 如果字符串中不是以单词或者数字结尾，去掉最后一个字符
        /*try {
            String urlName = prefix + FileConstant.WORD_AUDIO + encode(text) + ".mp3";
            URL url = new URL(urlName);
            url.openConnection().getInputStream();
            // 当前单词读音已经保存，直接返回链接地址
            log.info("单词[{}]读音已存在，读音地址[{}]", text, urlName);
            return urlName;
        } catch (IOException e) {
            // 当前单词读音还没有保存，保存单词读音并返回链接地址
            log.info("单词[{}]读音已不存在，保存读音", text);
            return saveWordAudio(text);
        }*/
    }

    private String saveWordAudio(String text) {

        String fileName = UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis() + ".mp3";

        Map<String, String> map = packageParam(text);

        try {
            text = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        String ttsUrl = getUrlWithQueryString(url, map);
        String ttsUrl;
        if (wordMap.containsKey(text)) {
            ttsUrl = "http://dict.youdao.com/dictvoice?audio=" + text + "&type=1";
		} else {
            ttsUrl = "http://dict.youdao.com/dictvoice?audio=" + text;
        }


        byte[] result = requestTTSorHttp(ttsUrl);
        //合成成功
        if (result != null) {
            try {
                ftpUtil.uploadByInputStream(new ByteArrayInputStream(result), FileConstant.WORD_AUDIO, fileName);
            } catch (Exception e) {
                log.error("单词[{}]读音上传到服务器失败！", text, e);
                return null;
            }
        }
        return FileConstant.WORD_AUDIO + fileName;
    }

    /**
     * 请求语音合成服务
     *
     * @param ttsUrl
     * @return
     */
    private static byte[] requestTTSorHttp(String ttsUrl) {
        byte[] result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(ttsUrl);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            Header[] contentType = httpResponse.getHeaders("Content-Type");
            //如果响应是wav
            if ("audio/mp3".equals(contentType[0].getValue()) || "audio/mpeg".equals(contentType[0].getValue())) {
                HttpEntity httpEntity = httpResponse.getEntity();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(baos);
                result = baos.toByteArray();
                EntityUtils.consume(httpEntity);
            } else {
                /** 响应不是音频流，直接显示结果 */
                HttpEntity httpEntity = httpResponse.getEntity();
                String json = EntityUtils.toString(httpEntity, "UTF-8");
                EntityUtils.consume(httpEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String getUrl() {
        Map<String, String> map = packageParam("");
        return getUrlWithQueryString(url, map) + "&q=";
    }

    private Map<String, String> packageParam(String text) {
        Map<String, String> map = new HashMap<>(16);
        map.put("appKey", appKey);
        map.put("langType", "en");
        map.put("salt", "123");
        map.put("voice", "6");
        if (!StringUtils.isEmpty(text)) {
            map.put("q", text);
        }
        String sign = md5(
                map.get("appKey") + map.get("q") + map.get("salt") + md5Key);
        map.put("sign", sign);
        return map;
    }

    /**
     * 根据api地址和参数生成请求URL
     *
     * @param url
     * @param params
     * @return
     */
    private static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            // 过滤空的key
            if (value == null) {
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }

    /**
     * 进行URL编码
     *
     * @param input
     * @return
     */
    private static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }

    /**
     * 生成32位MD5摘要
     *
     * @param string
     * @return
     */
    private static String md5(String string) {
        if (string == null) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes();
        try {
            /** 获得MD5摘要算法的 MessageDigest 对象 */
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            /** 使用指定的字节更新摘要 */
            mdInst.update(btInput);
            /** 获得密文 */
            byte[] md = mdInst.digest();
            /** 把密文转换成十六进制的字符串形式 */
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
