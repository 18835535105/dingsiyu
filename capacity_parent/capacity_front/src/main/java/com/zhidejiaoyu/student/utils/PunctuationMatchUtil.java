package com.zhidejiaoyu.student.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Stack;
import java.util.regex.Pattern;

/**
 * 英文句子格式化空格工具类
 * <p>句子中标点之后若是单词，将标点之后空一个格</p>
 *
 *  @author wuchenxi
 * @date 2018/6/6 10:37
 */
@Slf4j
public class PunctuationMatchUtil {

    private static final String MATCHES = "[a-zA-Z\\s]";

    private static final Stack<String> STACK = new Stack<>();

    /**
     * 格式化例句
     *
     * @param str 目标例句
     * @return 格式化后的例句
     */
    public static String matchPunctuation(String str) {
        STACK.clear();
        matchAndUpdate(str);
        return print();
    }

    private static void matchAndUpdate(String str) {
        char[] charArray = str.toCharArray();
        for (Character character : charArray) {
            String char2String = char2String(character);
            if (STACK.isEmpty()) {
                STACK.push(char2String);
                continue;
            }

            boolean currentIsMatch = Pattern.matches(MATCHES, char2String);
            if (currentIsMatch) {
                boolean previousIsMatch = Pattern.matches(MATCHES, STACK.peek());

                // 如果上个入栈的字符是空格，当前字符也是空格，则当前字符不入栈，避免多个空格
                boolean isBlank = Pattern.matches("\\s", char2String);
                if (Pattern.matches("\\s", STACK.peek()) && isBlank) {
                    continue;
                }

                if (!previousIsMatch && !isBlank) {
                    log.info("句子 [{}] 单词后面紧跟符号，没有空格！", str);
                    STACK.push(" ");
                }
            }
            STACK.push(char2String);

        }
    }

    private static String print() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String aSTACK : STACK) {
            stringBuilder.append(aSTACK);
        }
        return stringBuilder.toString();
    }

    private static String char2String(char character) {
        char[] chars = new char[1];
        chars[0] = character;
        return new String(chars);
    }

    public static void main(String[] args) {
        String str = "I felt as if I had stumbled into a nightmare country, as you sometimes do in dreams.";
        System.out.println(PunctuationMatchUtil.matchPunctuation(str));
    }
}
