package com.zhidejiaoyu.student.utils.sensitiveword;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 敏感词过滤
 *
 * @author wuchenxi
 */
@Component
public class SensitiveWordFilter {

    @Autowired
    private SensitiveWordInit sensitiveWordInit;

    private Map sensitiveWordMap = null;
    /**
     * 最小匹配原则，例如敏感词有“中国”和“中国人”，匹配到“中国”这个敏感词时就返回结果
     */
    public static int minMatchTYpe = 1;
    /**
     * 最大匹配原则，例如敏感词有“中国”和“中国人”，匹配到“中国人”这个敏感词时才返回结果
     */
    public static int maxMatchType = 2;

    /**
     * 初始化敏感词
     */
    public void init() {
        sensitiveWordMap = sensitiveWordInit.initKeyWord();
    }

    /**
     * 判断文本中是否含有敏感词
     *
     * @param txt
     * @param matchType
     * @return
     */
    public boolean isContainsSensitiveWord(String txt, int matchType) {
        boolean flag = false;
        for (int i = 0; i < txt.length(); i++) {
            int matchFlag = this.CheckSensitiveWord(txt, i, matchType);
            if (matchFlag > 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 获取文本中的敏感词
     *
     * @param txt
     * @param matchType
     * @return
     */
    public Set<String> getSensitiveWord(String txt, int matchType) {
        Set<String> sensitiveWordList = new HashSet<String>();

        for (int i = 0; i < txt.length(); i++) {
            int length = CheckSensitiveWord(txt, i, matchType);
            if (length > 0) {
                sensitiveWordList.add(txt.substring(i, i + length));
                i = i + length - 1;
            }
        }

        return sensitiveWordList;
    }

    /**
     * 替换文本中的敏感词
     *
     * @param txt
     * @param matchType
     * @param replaceChar   将敏感词替换为的目标字符
     * @return
     */
    public String replaceSensitiveWord(String txt, int matchType, String replaceChar) {
        String resultTxt = txt;
        Set<String> set = getSensitiveWord(txt, matchType);
        Iterator<String> iterator = set.iterator();
        String word;
        String replaceString;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }

        return resultTxt;
    }

    /**
     * 获取替换的字符
     *
     * @param replaceChar
     * @param length
     * @return
     */
    private String getReplaceChars(String replaceChar, int length) {
        StringBuilder resultReplace = new StringBuilder(replaceChar);
        for (int i = 1; i < length; i++) {
            resultReplace.append(replaceChar);
        }

        return resultReplace.toString();
    }


    public int CheckSensitiveWord(String txt, int beginIndex, int matchType) {
        boolean flag = false;
        int matchFlag = 0;
        char word = 0;
        Map nowMap = sensitiveWordMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);
            if (nowMap != null) {
                matchFlag++;
                if ("1".equals(nowMap.get("isEnd"))) {
                    flag = true;
                    if (minMatchTYpe == matchType) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
        if (matchFlag < 2 || !flag) {
            matchFlag = 0;
        }
        return matchFlag;
    }
}
