package com.zhidejiaoyu.common.utils.simple.language;

import com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.utils.simple.http.SimpleHttpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * 讯飞语音评测工具
 * <p>返回结果说明地址：http://doc.xfyun.cn/ise_protocol/%E8%AF%84%E6%B5%8B%E7%BB%93%E6%9E%9C%E6%A0%BC%E5%BC%8F.html</p>
 *
 * @author wuchenxi
 * @date 2018/6/20 17:01
 */
@Component
public class SimpleSpeechEvaluation {

    private Logger logger = LoggerFactory.getLogger(SimpleSpeechEvaluation.class);

    @Autowired
    private SimpleHttpClientUtil simpleHttpClientUtil;

    @Value("${xfyun.ise.URL}")
    private String URL;

    @Value("${xfyun.ise.APP_ID}")
    private String APP_ID;

    @Value("${xfyun.ise.API_KEY}")
    private String API_KEY;

    /**
     * 获取语音评测得分
     *
     * @param text    评测的内容
     * @param fileUrl 语音文件地址
     * @return 测试得分（小数）
     */
    public Double getEvaluationRecord(String text, String fileUrl) {
        String result = this.getEvaluationResult(text, fileUrl);
        String record = JSONObject.parseObject(result).getJSONObject("data").getJSONObject("read_sentence").getJSONObject("rec_paper").getJSONObject("read_chapter").get("total_score").toString();
        return Double.valueOf(record);
    }

    /**
     * 获取评测结果
     *
     * @param text    评测的内容
     * @param fileUrl 语音文件地址
     * @return 测试结果，结果详细说明地址：
     * <a href="http://doc.xfyun.cn/ise_protocol/%E8%AF%84%E6%B5%8B%E7%BB%93%E6%9E%9C%E6%A0%BC%E5%BC%8F.html">
     * http://doc.xfyun.cn/ise_protocol/%E8%AF%84%E6%B5%8B%E7%BB%93%E6%9E%9C%E6%A0%BC%E5%BC%8F.html
     * </a>
     */
    public String getEvaluationResult(String text, String fileUrl) {

        // 读取音频文件
        FileOutputStream fileOutputStream = getFileOutputStream(fileUrl);

        String mp3Bytes = Base64.getEncoder().encodeToString(fileOutputStream.toString().getBytes());
        try {
            mp3Bytes = URLEncoder.encode(mp3Bytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 请求参数
        Map<String, String> map = new HashMap<>(16);
        map.put("aue", "raw");
        map.put("language", "en");
        map.put("category", "read_sentence");

        String param = JSONObject.toJSON(map).toString();
        String X_param = Base64.getMimeEncoder().encodeToString(param.getBytes());
        String currentTime = String.valueOf(System.currentTimeMillis() / 1000);

        CloseableHttpClient httpClient = simpleHttpClientUtil.getHttpClient();
        HttpPost post = new HttpPost(URL);
        post.addHeader("X-Appid", APP_ID);
        post.addHeader("X-CurTime", currentTime);
        post.addHeader("X-Param", X_param);
        post.addHeader("X-CheckSum", new Md5Hash(API_KEY + currentTime + X_param).toString());

        map.put("audio", mp3Bytes);
        map.put("text", text);

        List<BasicNameValuePair> param1 = new ArrayList<>();
        map.forEach((key, value) -> {
            if (value != null) {
                param1.add(new BasicNameValuePair(key, value));
            }
        });

        String result = null;
        CloseableHttpResponse response = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(param1, "UTF-8"));
            response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private FileOutputStream getFileOutputStream(String fileUrl) {
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(fileUrl);
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("语音评测获取语音文件失败！路径： {}", fileUrl, e.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileOutputStream;
    }
}