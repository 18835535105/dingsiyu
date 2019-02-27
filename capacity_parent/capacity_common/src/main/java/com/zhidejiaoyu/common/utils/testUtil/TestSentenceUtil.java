package com.zhidejiaoyu.common.utils.testUtil;


import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.math.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * 句子测试题返回
 */
@Component
public class TestSentenceUtil {

    @Autowired
    private BaiduSpeak baiduSpeak;

    public List<Object> resultTestSentence(List<Sentence> sentences, List<Sentence> disturb) {
        List<Object> resultList = new ArrayList<>();
        List<Sentence> sentenceList = new ArrayList<>();
        for (int i = 0; i < sentences.size(); i++) {
            sentenceList.add(sentences.get(i));
        }
        if (disturb != null) {
            for (int i = 0; i < disturb.size(); i++) {
                sentenceList.add(disturb.get(i));
            }
        }
        for (int i = 0; i < sentences.size(); i++) {
            if(i==0){
                resultList.add(englishToChinese(sentences.get(i), sentenceList));
            }
            if(i!=0){
                int ran = MathUtil.getRandom(1, 10);
                if(ran>=5){
                    resultList.add(hearing(sentences.get(i), sentenceList));
                }
                if(ran<5 && ran>=3){
                    resultList.add(chineseToEnglish(sentences.get(i), sentenceList));
                }
                if(ran>=1&&ran<3){
                    resultList.add(englishToChinese(sentences.get(i), sentenceList));
                }
            }


        }
        return resultList;
    }

    //英译汉
    private Map<String, Object> hearing(Sentence sentences, List<Sentence> disturb) {
        List<String> anawerList = new ArrayList<>();
        if (disturb != null) {
            for (int i = 0; i < disturb.size(); i++) {
                anawerList.add(disturb.get(i).getCentreExample().replace("#", " "));
            }
        }
        Map<String, Object> selMap = new HashMap<>();
        String english = sentences.getCentreExample().replace("#", " ");
        String chinese = sentences.getCentreTranslate().replace("*", "");
        selMap.put("english", english);
        selMap.put("chinese", chinese);
        selMap.put("hearing", baiduSpeak.getSentencePath(sentences.getCentreExample()));
        selMap.put("type", "hearing");
        arrange(anawerList, english, selMap);
        return selMap;
    }

    //英译汉
    private Map<String, Object> englishToChinese(Sentence sentences, List<Sentence> disturb) {
        List<String> anawerList = new ArrayList<>();
        if (disturb != null) {
            for (int i = 0; i < disturb.size(); i++) {
                anawerList.add(disturb.get(i).getCentreTranslate().replace("*", ""));
            }
        }
        Map<String, Object> selMap = new HashMap<>();
        String english = sentences.getCentreExample().replace("#", " ");
        String chinese = sentences.getCentreTranslate().replace("*", "");
        selMap.put("english", english);
        selMap.put("chinese", chinese);
        selMap.put("hearing", baiduSpeak.getSentencePath(sentences.getCentreExample()));
        selMap.put("type", "english");
        arrange(anawerList, chinese, selMap);
        return selMap;
    }

    //汉译英
    private Map<String, Object> chineseToEnglish(Sentence sentences, List<Sentence> disturb) {
        List<String> anawerList = new ArrayList<>();
        if (disturb != null) {
            for (int i = 0; i < disturb.size(); i++) {
                anawerList.add(disturb.get(i).getCentreExample().replace("#", " "));
            }
        }
        Map<String, Object> selMap = new HashMap<>();
        String english = sentences.getCentreExample().replace("#", " ");
        String chinese = sentences.getCentreTranslate().replace("*", "");
        selMap.put("english", english);
        selMap.put("chinese", chinese);
        selMap.put("hearing", baiduSpeak.getSentencePath(sentences.getCentreExample()));
        selMap.put("type", "chinese");
        arrange(anawerList, english, selMap);
        return selMap;
    }

    //选项排序及确定选项位置
    private void arrange(List<String> anawer, String correct, Map map) {
        List<String> strings = new ArrayList<>();
        List<String> option  = new ArrayList<>();
        Map<String, String> isMap = new HashMap<>();
        isMap.put(correct, correct);
        strings.add(correct);
        while (isMap.size() < 4) {
            int i = (int) (Math.random() * anawer.size());
            isMap.put(anawer.get(i), anawer.get(i));
            if (isMap.size() > strings.size()) {
                strings.add(anawer.get(i));
            }
        }
        Collections.shuffle(strings);
        for (int i = 0; i < strings.size(); i++) {
            if (strings.get(i).equals(correct)) {
                map.put("answer", i);
            }
            option.add(strings.get(i));
        }
        map.put("option", option);
    }
}
