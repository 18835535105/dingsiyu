package com.zhidejiaoyu.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 *
 * @author xiaoleilu
 */
public class StringUtil {

    /**
     * 特殊的空格
     */
    public static final String SPECIAL_SPACE = " ";

    /**
     * 普通空格
     */
    public static final String SPACE = " ";
    public static final String DOT = ".";
    public static final String SLASH = "/";
    public static final String BACKSLASH = "\\";

    /**
     * 空字符串
     */
    public static final String EMPTY = "";
    public static final String CRLF = "\r\n";
    public static final String NEWLINE = "\n";
    public static final String UNDERLINE = "_";
    public static final String COMMA = ",";

    public static final String HTML_NBSP = "&nbsp;";
    public static final String HTML_AMP = "&amp";
    public static final String HTML_QUOTE = "&quot;";
    public static final String HTML_LT = "&lt;";
    public static final String HTML_GT = "&gt;";

    public static final String EMPTY_JSON = "{}";

    /**
     * 去除字符串前后空格
     *
     * @param str
     * @return
     */
    public static String trim(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return StringUtils.trim(str).replace(SPECIAL_SPACE, "");
    }

    /**
     * 将特殊的空格替换为普通空格
     *
     * @param str
     * @return
     */
    public static String replaceSpecialSpaceToNormalSpace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.replace(SPECIAL_SPACE, SPACE);
    }

    public static boolean isEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
