package com.zhidejiaoyu.common.utils.duration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 获取市场表中的学习模块
 *
 * @author: wuchenxi
 * @date: 2020/2/18 11:22:22
 */
public class DurationStudyModelUtil {
    /**
     * 单词模块
     */
    private static final Integer[] WORD = {0, 1, 2, 3, 14, 15, 16, 17, 18, 19, 20, 21, 22, 27, 35, 40};

    /**
     * 句型模块
     */
    private static final Integer[] SENTENCE = {4, 5, 6, 19};
    /**
     * 课文模块
     */
    private static final Integer[] TEKS = {28, 29, 30, 31};
    /**
     * 语法模块
     */
    private static final Integer[] SYNTAX = {37, 38, 39};
    /**
     * 字母模块
     */
    private static final Integer[] LETTER = {30, 21, 32, 33, 34};

    /**
     * 金币试卷
     */
    private static final Integer[] GOLD_TEST = {41};

    /**
     * 音标模块
     */
    private static final Integer[] PHONETIC_SYMBOL = {31, 34};

    private static final Map<Integer[], String> MODEL_TYPE = new HashMap<>(16);

    static {
        MODEL_TYPE.put(WORD, "单词");
        MODEL_TYPE.put(SENTENCE, "句型");
        MODEL_TYPE.put(TEKS, "课文");
        MODEL_TYPE.put(SYNTAX, "语法");
        MODEL_TYPE.put(LETTER, "字母");
        MODEL_TYPE.put(PHONETIC_SYMBOL, "音标");
        MODEL_TYPE.put(GOLD_TEST, "金币测试");
    }

    /**
     * 获取学习所属模块
     *
     * @param studyModel
     * @return
     */
    public static String getStudyModelStr(Integer studyModel) {
        Set<Integer[]> integers = MODEL_TYPE.keySet();
        for (Integer[] integer : integers) {
            if (Arrays.asList(integer).contains(studyModel)) {
                return MODEL_TYPE.get(integer);
            }
        }
        return "";
    }

    private DurationStudyModelUtil() {
    }
}
