package com.zhidejiaoyu.common.utils.grade;

import java.util.*;

/**
 * 标签工具类
 *
 * @author: wuchenxi
 * @date: 2019/12/21 11:23:23
 */
public class LabelUtil {

    private static final String[] GRADE = {"二级", "三级"};
    private static final String[] UP_OR_DOWN = {"上册", "下册"};
    private static final String[] REQUIRED = {"必修一", "必修二", "必修三", "必修四", "必修五", "选修六", "选修七", "选修八", "选修九", "选修十", "选修十一"};
    private static final String[] CORE_VOCABULARY = {"核心词汇一", "核心词汇二", "核心词汇三"};
    private static final String[] EASY_OR_HARD = {"易", "中", "难"};
    private static final String[] CE = {"第一册", "第二册", "第三册", "第四册", "第五册", "第六册", "第七册", "第八册", "第九册", "第十册"};
    private static final String[] MIDDLE_UP_OR_DOWN = {"上册（初中）", "下册（初中）"};
    private static final String[] OUTLINE_VOCABULARY = {"大纲词汇一", "大纲词汇二", "大纲词汇三", "大纲词汇四", "大纲词汇五", "大纲词汇六", "大纲词汇七",
            "大纲词汇八", "大纲词汇九", "大纲词汇十", "大纲词汇十一", "大纲词汇十二", "大纲词汇十三"};
    private static final String[] NUMBER = {"0", "1", "2", "3", "4", "5", "6"};
    private static final String[] SIMPLE = {"入门级A", "入门级B"};
    private static final String[] NUMBER_TAG = {"1 A", "1 B", "2 A", "2 B", "3 A", "3 B"};

    private static Map<String, String[]> map;

    static {
        map = new HashMap<>(16);
        for (String s : GRADE) {
            map.put(s, GRADE);
        }
        for (String s : UP_OR_DOWN) {
            map.put(s, UP_OR_DOWN);
        }
        for (String s : REQUIRED) {
            map.put(s, REQUIRED);
        }
        for (String s : CORE_VOCABULARY) {
            map.put(s, CORE_VOCABULARY);
        }
        for (String s : EASY_OR_HARD) {
            map.put(s, EASY_OR_HARD);
        }
        for (String s : CE) {
            map.put(s, CE);
        }
        for (String s : MIDDLE_UP_OR_DOWN) {
            map.put(s, MIDDLE_UP_OR_DOWN);
        }
        for (String s : OUTLINE_VOCABULARY) {
            map.put(s, OUTLINE_VOCABULARY);
        }
        for (String s : NUMBER) {
            map.put(s, NUMBER);
        }
        for (String s : SIMPLE) {
            map.put(s, SIMPLE);
        }
        for (String s : NUMBER_TAG) {
            map.put(s, NUMBER_TAG);
        }
    }

    /**
     * 小于当前标签
     *
     * @param label
     * @return
     */
    public static List<String> getLessThanCurrentLabel(String label) {

        List<String> list = new ArrayList<>();
        String[] labels = map.get(label);
        if (labels == null) {
            list.add(label);
            return list;
        }

        for (String label1 : labels) {
            if (list.isEmpty() || !Objects.equals(label1, label)) {
                list.add(label1);
            }
            if (Objects.equals(label1, label)) {
                break;
            }
        }
        return list;
    }

    private LabelUtil() {
    }
}
