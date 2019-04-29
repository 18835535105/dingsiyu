package com.zhidejiaoyu.student.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhidejiaoyu.common.utils.language.SpeechEvaluation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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
        if (result != null) {
            Map<String, Object> map = new HashMap<>(16);
            boolean flag = this.getIsRejected(result);
            int score;
            if (flag) {
                score = 0;
            } else {
                score = (int) Math.round(Double.valueOf(getReadChapterJsonObject(result).getString("total_score")) * 20);
            }
            map.put("score", score);
            map.put("heart", getHeart(score));
            return map;
        }
        return new HashMap<>(16);

    }

    /**
     * 判断用户是否是乱读
     *
     * @param result    true：用户属于乱读；false：用户不是乱读
     * @return
     */
    private boolean getIsRejected(String result) {
        String string = JSONObject.parseObject(result).getJSONObject("data").getJSONObject("read_sentence").
                getJSONObject("rec_paper").getJSONObject("read_chapter").getString("is_rejected");
        return Objects.equals("true", string);
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
        String findText = text.replace("!", ",").replace("?", ",").replace(".", ",");
        String result = speechEvaluation.getEvaluationResult(findText, fileUrl);
        if (result != null) {

            JSONObject readChapter = getReadChapterJsonObject(result);

            Map<String, Object> map = new HashMap<>(16);
            List<Map<String, Object>> mapList = new ArrayList<>();
            Map<String, Object> wordMap;

            map.put("totalScore", (int) Math.round(Double.valueOf(readChapter.getString("total_score")) * 20));
            map.put("heart", getHeart((int) map.get("totalScore")));

            int score;
            String[] s = text.split(" ");

            if (s.length > 1) {
                Map<Integer,Integer> ifMap=new HashMap<>();
                JSONObject sentenceObject = readChapter.getJSONObject("sentence");
                JSONArray wordArray = sentenceObject.getJSONArray("word");
                int j = 0;
                for (int i = 0; i < wordArray.size(); i++) {
                    JSONObject wordJsonObject = (JSONObject) wordArray.get(i);
                    Integer index = wordJsonObject.getInteger("index");
                    if (index == null) {
                        continue;
                    }
                    if(ifMap.get(index)!=null){
                        continue;
                    }
                    ifMap.put(index,index);
                    if (StringUtils.isNotEmpty(wordJsonObject.getString("total_score"))) {
                        score = (int) Math.round(Double.valueOf(wordJsonObject.getString("total_score")) * 20);
                        wordMap = new HashMap<>(16);
                        if (s[j].endsWith(",") || s[j].endsWith("!") || s[j].endsWith("?") || s[j].endsWith(".")) {
                            wordMap.put("word", s[j].substring(0, s[j].length() - 1));
                        } else {
                            wordMap.put("word", s[j]);
                        }
                        wordMap.put("score", score);
                        wordMap.put("color", getColor(score));
                        mapList.add(wordMap);
                    }
                    // todo:数组下标越界异常

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
                score = (int) Math.round(Double.valueOf(getReadChapterJsonObject(result).getString("total_score")) * 20);
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

    /**
     * @param result
     * @return
     */
    private JSONObject getReadChapterJsonObject(String result) {
        JSONObject resultObject = JSONObject.parseObject(result);
        if (!Objects.equals("0", resultObject.getString("code"))) {
            log.error("获取学生语音评测信息出错: {}", resultObject.toString());
        }
        return JSONObject.parseObject(result).getJSONObject("data").getJSONObject("read_sentence")
                .getJSONObject("rec_paper").getJSONObject("read_chapter");
    }
}
