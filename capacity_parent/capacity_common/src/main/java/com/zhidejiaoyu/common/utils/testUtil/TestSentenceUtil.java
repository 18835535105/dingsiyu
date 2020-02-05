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

    public List<Object> resultTestSentence(List<Sentence> sentences, List<Sentence> disturb, Integer type) {
        List<Object> resultList = new ArrayList<>();
        List<Sentence> sentenceList = new ArrayList<>(sentences);
        if (disturb != null) {
            sentenceList.addAll(disturb);
        }
        for (int i = 0; i < sentences.size(); i++) {
            if (type == 1) {
                resultList.add(hearing(sentences.get(i), sentenceList));
            }
            if (type == 2) {
                int ran = MathUtil.getRandom(1, 10);
                if (ran >= 1 && ran < 5) {
                    resultList.add(chineseToEnglish(sentences.get(i), sentenceList));
                }
                if (ran >= 5 && ran <= 10) {
                    resultList.add(englishToChinese(sentences.get(i), sentenceList));
                }
            }
        }
        return resultList;
    }

    //英译汉
    private Map<String, Object> hearing(Sentence sentences, List<Sentence> disturb) {
        Map<String, Object> selMap = getEnglish(sentences, disturb, 2);
        selMap.put("type", "hearing");
        return selMap;
    }

    //英译汉
    private Map<String, Object> englishToChinese(Sentence sentences, List<Sentence> disturb) {
        Map<String, Object> selMap = getEnglish(sentences, disturb, 1);
        selMap.put("type", "english");
        return selMap;
    }

    //汉译英
    private Map<String, Object> chineseToEnglish(Sentence sentences, List<Sentence> disturb) {
        Map<String, Object> selMap = getEnglish(sentences, disturb, 3);
        selMap.put("type", "chinese");
        return selMap;
    }

    private Map<String, Object> getEnglish(Sentence sentences, List<Sentence> disturb, Integer type) {
        List<String> anawerList = new ArrayList<>();
        List<String> chineseList = new ArrayList<>();
        if (disturb != null) {
            disturb.forEach(dis -> {
                anawerList.add(dis.getCentreExample().replace("#", " ").replace("$", ""));
                chineseList.add(dis.getCentreTranslate().replace("#", " ").replace("*", ""));
            });
        }
        Map<String, Object> selMap = new HashMap<>();
        String english = sentences.getCentreExample().replace("#", " ").replace("$", "");
        String chinese = sentences.getCentreTranslate().replace("*", "");
        selMap.put("english", english);
        selMap.put("chinese", chinese);
        selMap.put("hearing", baiduSpeak.getSentencePath(sentences.getCentreExample().replace("$", "")));
        if (type.equals(1)) {
            arrange(chineseList, chinese, selMap);
        } else if(type.equals(2)){
            int ran = MathUtil.getRandom(1, 10);
            if (ran >= 1 && ran < 5) {
                arrange(anawerList, english, selMap);
            }
            if (ran >= 5 && ran <= 10) {
                arrange(chineseList, chinese, selMap);
            }
        }else{
            arrange(anawerList, english, selMap);

        }

        return selMap;
    }

    //选项排序及确定选项位置
    private void arrange(List<String> anawer, String correct, Map map) {
        List<String> strings = new ArrayList<>();
        List<String> option = new ArrayList<>();
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
