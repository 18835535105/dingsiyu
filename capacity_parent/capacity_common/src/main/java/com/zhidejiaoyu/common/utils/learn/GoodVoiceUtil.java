package com.zhidejiaoyu.common.utils.learn;

import com.alibaba.csp.ahas.shaded.com.alibaba.fastjson.JSONArray;
import com.alibaba.csp.ahas.shaded.com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.utils.language.SpeechEvaluation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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
     * @param text 评测的内容
     * @param file 语音文件
     * @return 测试得分（小数）；满分为 5 分
     */
    public Map<String, Object> getWordEvaluationRecord(String text, MultipartFile file) {
        String result = speechEvaluation.getEvaluationResult(text, file);
        if (log.isDebugEnabled()) {
            log.debug("语音评测响应结果：[{}]", result);
        }
        if (result != null) {
            Map<String, Object> map = new HashMap<>(16);

            int score;
            Float pronAccuracy = JSONObject.parseObject(result).getFloat("PronAccuracy");
            String audioUrl = JSONObject.parseObject(result).getString("AudioUrl");
            if (pronAccuracy == -1) {
                score = 0;
            } else {
                score = Math.round(pronAccuracy);
            }
            map.put("score", score);
            map.put("heart", getHeart(score));
            map.put("voiceUrl", audioUrl);
            return map;
        }
        return new HashMap<>(16);

    }

    /**
     * 获取例句的语音评测
     *
     * @param text
     * @param file
     * @return 满分为 5 分
     * key:totalScore; 句子总评分<br>
     * key:word; value:List    单词评分信息<br>
     * key:score; 单词评分<br>
     * key:word; 单词英文<br>
     * <p>
     */
    public Map<String, Object> getSentenceEvaluationRecord(String text, MultipartFile file) {
        // 句子中各个单词得分信息
        final String words = "Words";

        String findText = text.replace("!", ",").replace("?", ",").replace(".", ",");
        String result = speechEvaluation.getEvaluationResult(findText, file);
        if (log.isDebugEnabled()) {
            log.debug("语音评测响应结果：[{}]", result);
        }
        if (result != null) {
            Map<String, Object> map = new HashMap<>(16);
            List<Map<String, Object>> mapList = new ArrayList<>();

            JSONObject jsonObject = JSONObject.parseObject(result);

            int totalScore = this.getScore(jsonObject);

            map.put("totalScore", totalScore);
            map.put("heart", getHeart(totalScore));
            String audioUrl = JSONObject.parseObject(result).getString("AudioUrl");
            map.put("voiceUrl", audioUrl);

            int score;
            String[] s = text.split(" ");

            JSONArray wordArray = jsonObject.getJSONArray(words);
            // 存储单词
            Map<String, Object> wordMap;
            // 存储标点
            Map<String, Object> pointMap;
            if (s.length > 1) {
                // 语音评测结果为空或者评测结果数据比文本数据多（比如文本中含有 “25”这个数字，评测结果会把它拆分成两个单词），默认各个单词默认分数为 0
                if (wordArray.size() == 0 || wordArray.size() > s.length) {
                    packageDefaultSentenceGoodVoiceResult(mapList, s);
                    map.put("word", mapList);
                    return map;
                }
                // 语音评测结果不为空
                int j = 0;
                for (Object o : wordArray) {
                    JSONObject wordJsonObject = (JSONObject) o;
                    if ("*".equals(wordJsonObject.getString("Word")) || ",".equals(wordJsonObject.getString("Word"))) {
                        continue;
                    }
                    score = getScore(wordJsonObject);
                    packageSentenceGoodVoiceResultMap(mapList, score, s[j]);
                    j++;
                }
            } else {
                wordMap = new HashMap<>(16);
                if (wordArray.size() == 0) {
                    packageDefaultSentenceGoodVoiceResult(mapList, s);
                    map.put("word", mapList);
                    return map;
                }
                score = getScore(wordArray.getJSONObject(0));
                wordMap.put("word", s[0].substring(0, s[0].length() - 1));
                wordMap.put("score", score);
                wordMap.put("color", getColor(score));
                wordMap.put("heart", getHeart(score));
                mapList.add(wordMap);

                pointMap = new HashMap<>(16);
                pointMap.put("word", s[0].substring(s[0].length() - 1));
                pointMap.put("color", "#fff");
                mapList.add(pointMap);
            }
            map.put("word", mapList);
            return map;
        }
        return new HashMap<>(16);
    }

    private int getScore(JSONObject jsonObject) {
        int score = Math.round(jsonObject.getFloat("PronAccuracy"));
        return score == -1 ? 0 : score;
    }

    /**
     * 封装句子语音评测默认结果（没有评测结果或者封装响应数据出错）
     *
     * @param mapList 需要响应的数据集合
     * @param words   句子中单词数组
     */
    private void packageDefaultSentenceGoodVoiceResult(List<Map<String, Object>> mapList, String[] words) {
        for (String word : words) {
            packageSentenceGoodVoiceResultMap(mapList, 0, word);
        }
    }

    /**
     * 封装句子语音评测响应结果
     *
     * @param mapList 需要响应的数据集合
     * @param score   得分
     * @param word    当前单词
     */
    private void packageSentenceGoodVoiceResultMap(List<Map<String, Object>> mapList, int score, String word) {
        Map<String, Object> wordMap;
        Map<String, Object> pointMap;
        wordMap = new HashMap<>(16);
        boolean endWithPoint = word.endsWith(",") || word.endsWith("!") || word.endsWith("?") || word.endsWith(".");
        if (endWithPoint) {
            wordMap.put("word", word.substring(0, word.length() - 1));
        } else {
            wordMap.put("word", word);
        }
        wordMap.put("pronAccuracy", score);
        wordMap.put("color", getColor(score));
        mapList.add(wordMap);

        if (endWithPoint) {
            pointMap = new HashMap<>(16);
            pointMap.put("word", word.substring(word.length() - 1));
            pointMap.put("color", "#fff");
            mapList.add(pointMap);
        }
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
