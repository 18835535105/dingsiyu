package com.zhidejiaoyu.common.study.simple;

import com.zhidejiaoyu.common.mapper.simple.SimpleLearnMapper;
import com.zhidejiaoyu.common.pojo.Learn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 学生用户使用过程中重复使用的功用方法
 *
 * @author wuchenxi
 * @date 2018/5/21 15:34
 */
@Slf4j
@Component
public class SimpleCommonMethod implements Serializable {

    /**
     * 标点数组
     */
    private final String[] POINT = {".", ",", "?", "!", "，", "。", "？", "！", "、", "：", "“", "”", "《", "》"};

    @Autowired
    private SimpleLearnMapper learnMapper;

    /**
     * 学生第一次学习完成学习引导页内容后，将该学生新信息置为不是第一次学习，可直接进入学习页面
     *
     * @param stuId      当前登录学生id
     * @param studyModel 学习模块 （慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写）
     */
    public void clearFirst(Long stuId, String studyModel) {
        Learn learn = new Learn();
        learn.setStudentId(stuId);
        learn.setStudyModel(studyModel);
        learnMapper.insert(learn);
    }

    /**
     * 将例句顺序分割，其中的标点单独占用一个下标
     *
     * @param sentence 需要处理的例句
     * @return
     */
    public List<String> getEnglishList(String sentence) {
        for (String s : POINT) {
            if (sentence.contains(s)) {
                sentence = sentence.replace(s, " " + s);
            }
        }

        List<String> list = new ArrayList<>();
        String[] arr = sentence.split(" ");
        for (String s : arr) {
            if (s.contains("#")) {
                list.add(s.replace("#", " "));
            } else {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * 将例句单词顺序打乱
     *
     * @param sentence
     * @param exampleDisturb 例句英文干扰项
     * @return
     */
    public List<String> getOrderEnglishList(String sentence, String exampleDisturb) {
        // 去除标点
        for (String s : POINT) {
            if (sentence.contains(s)) {
                sentence = sentence.replace(s, "");
            }
        }

        // 将例句按照空格拆分
        String[] words = sentence.split(" ");

        List<String> list = new ArrayList<>();
        // 去除固定搭配中的#
        for (int i = 0; i < words.length; i++) {
            if (words[i].contains("#")) {
                words[i] = words[i].replace("#", " ");
            }
            list.add(words[i]);
        }

        list.add(exampleDisturb);
        Collections.shuffle(list);
        return list;
    }

    /**
     * 获取乱序的中文选项
     *
     * @param sentence         例句中文
     * @param translateDisturb 中文干扰项
     * @return
     */
    public List<String> getOrderChineseList(String sentence, String translateDisturb) {
        // 去除标点
        for (String s : POINT) {
            if (sentence.contains(s)) {
                sentence = sentence.replace(s, " ");
            }
        }

        // 拆分并去除*
        List<String> list = new ArrayList<>();
        String[] arr = sentence.split("\\*");
        Collections.addAll(list, arr);
        list.add(translateDisturb);
        Collections.shuffle(list);
        return list;
    }

    /**
     * 将清学版测试类型转换为汉字
     *
     * @param classify 1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写
     * @return 测试类型中文
     */
    public String getTestType(Integer classify) {
        String[] typeStr = {"单词辨音", "词组辨音", "快速单词", "快速词组", "词汇考点", "快速句型", "语法辨析", "单词默写", "词组默写"};
        if (classify == null) {
            log.error("classify = null");
            return null;
        }
        if (classify <= typeStr.length) {
            return typeStr[classify - 1];
        }
        return null;
    }

    /**
     * 将智能版学习模块转换为汉字
     *
     * @param model 0：单词图鉴；1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写；7：五维测试
     * @return
     */
    public String getCapacityStudyModel(Integer model) {
        if (model == -1) {
            return "五维测试";
        }
        String[] typeStr = {"单词图鉴","慧记忆","慧听写","慧默写","例句听力","例句翻译","例句默写","五维测试"};
        int length = typeStr.length;
        if (model != null && model < length) {
            return typeStr[model];
        }
        return null;
    }

}
