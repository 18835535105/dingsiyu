package com.zhidejiaoyu.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 数组工具类
 *
 * @author wuchenxi
 * @date 2020-07-22 12:01:27
 */
public class ArrayUtil<T> {

    /**
     * 去除字符串数组中的空字符元素
     * 比如 {"123","", "4234", " "},去除空字符元素后数组为{"123", "4234"}
     *
     * @param array
     * @return 去除空字符元素的新数组
     */
    public static String[] removeBlankString(String[] array) {
        if (array == null || array.length == 0) {
            return array;
        }

        return Arrays.stream(array).filter(s -> StringUtils.isNotBlank(s) && StringUtils.isNotEmpty(s)).toArray(String[]::new);
    }

    /**
     * 数组去除字符串中的空字符元素后，返回相应类型的集合
     *
     * @param array
     * @return
     */
    public static List<String> removeBlankStringConvertToList(String[] array) {
        if (array == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(removeBlankString(array));
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(ArrayUtil.removeBlankString(new String[]{"123", "", "4234", " "})));
    }

    private ArrayUtil() {
    }
}