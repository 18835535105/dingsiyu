package com.zhidejiaoyu.student.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.utils.language.SpeechEvaluation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 好声音工具类
 *
 * @author wuchenxi
 * @date 2018/8/30
 */
@Component
@Slf4j
public class GoodVoiceUtil {

    @Autowired
    private SpeechEvaluation speechEvaluation;

    /**
     * 获取单词语音评测得分
     *
     * @param text    评测的内容
     * @param fileUrl 语音文件地址
     * @return 测试得分（小数）；满分为 5 分
     */
    public Map<String, Object> getWordEvaluationRecord(String text, String fileUrl) {
        String result = speechEvaluation.getEvaluationResult(text, fileUrl);
        log.info(result);
        if (result != null) {
            Map<String, Object> map = new HashMap<>(16);

            int score;
            Float pronAccuracy = JSONObject.parseObject(result).getFloat("PronAccuracy");
            if (pronAccuracy == -1) {
                score = 0;
            } else {
                score = Math.round(pronAccuracy);
            }
            map.put("score", score);
            map.put("heart", getHeart(score));
            return map;
        }
        return new HashMap<>(16);

    }

    /**
     * 获取例句的语音评测
     *
     * @param text
     * @param fileUrl
     * @return 满分为 5 分
     * key:totalScore; 句子总评分<br>
     * key:word; value:List    单词评分信息<br>
     * key:score; 单词评分<br>
     * key:word; 单词英文<br>
     * <p>
     */
    public Map<String, Object> getSentenceEvaluationRecord(String text, String fileUrl) {
        // 得分
        final String pronAccuracy = "PronAccuracy";
        // 句子中各个单词得分信息
        final String words = "Words";

        String findText = text.replace("!", ",").replace("?", ",").replace(".", ",");
        String result = speechEvaluation.getEvaluationResult(findText, fileUrl);
        log.info("评测结果：{}", result);
        if (result != null) {
            Map<String, Object> map = new HashMap<>(16);
            List<Map<String, Object>> mapList = new ArrayList<>();
            Map<String, Object> wordMap;

            JSONObject jsonObject = JSONObject.parseObject(result);

            int totalScore = Math.round(jsonObject.getFloat(pronAccuracy));
            if (totalScore == -1) {
                totalScore = 0;
            }

            map.put("totalScore", totalScore);
            map.put("heart", getHeart(totalScore));

            int score;
            String[] s = text.split(" ");

            JSONArray wordArray = jsonObject.getJSONArray(words);
            if (s.length > 1) {
                int j = 0;
                for (Object o : wordArray) {
                    JSONObject wordJsonObject = (JSONObject) o;
                    if ("*".equals(wordJsonObject.getString("Word")) || ",".equals(wordJsonObject.getString("Word"))) {
                        continue;
                    }
                    score = Math.round(wordJsonObject.getFloat(pronAccuracy));
                    if (score == -1) {
                        score = 0;
                    }
                    wordMap = new HashMap<>(16);
                    if (s[j].endsWith(",") || s[j].endsWith("!") || s[j].endsWith("?") || s[j].endsWith(".")) {
                        wordMap.put("word", s[j].substring(0, s[j].length() - 1));
                    } else {
                        wordMap.put("word", s[j]);
                    }
                    wordMap.put("pronAccuracy", score);
                    wordMap.put("color", getColor(score));
                    mapList.add(wordMap);

                    if (s[j].endsWith(",") || s[j].endsWith("!") || s[j].endsWith("?") || s[j].endsWith(".")) {
                        wordMap = new HashMap<>(16);
                        wordMap.put("word", s[j].substring(s[j].length() - 1));
                        wordMap.put("color", "#fff");
                        mapList.add(wordMap);
                    }
                    j++;
                }
            } else {
                wordMap = new HashMap<>(16);
                score = Math.round(wordArray.getJSONObject(0).getFloat(pronAccuracy));
                if (score == -1) {
                    score = 0;
                }
                wordMap.put("word", s[0].substring(0, s[0].length() - 1));
                wordMap.put("score", score);
                wordMap.put("color", getColor(score));
                wordMap.put("heart", getHeart(score));
                mapList.add(wordMap);
                Map<String, Object> msm = new HashMap<>();
                msm.put("word", s[0].substring(s[0].length() - 1));
                msm.put("color", "#fff");
                mapList.add(msm);
            }

            map.put("word", mapList);
            return map;
        }
        return null;
    }

    private int getHeart(int score) {
        if (score < 20) {
            return 0;
        } else if (score < 40) {
            return 1;
        } else if (score < 50) {
            return 2;
        } else if (score < 70) {
            return 3;
        } else if (score < 90) {
            return 4;
        } else {
            return 5;
        }
    }

    private String getColor(int score) {
        if (score >= 80) {
            return "#009007";
        } else if (score >= 60) {
            return "ff7200";
        } else {
            return "#b90202";
        }
    }
}
