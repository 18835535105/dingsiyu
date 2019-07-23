package com.zhidejiaoyu.student.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 生词手册相关
 *
 * @author wuchenxi
 * @date 2019-07-23
 */
@RestController
@RequestMapping("/newWords")
public class ReadWordController {


    public static void main(String[] args) {
        String str = "我感到*很幸运，因为*所有的老师*都给了*我很多鼓励，我也喜欢*我所学的*每一门功课：英语、历史、英国文学、计算机科学、数学、科学、体育、艺术、烹饪和法语。";
        str = str.replace("*", " ");
        String[] split = str.split(" ");
        // 争取顺序
        List<String> rightList = new ArrayList<>();
        // 存储标点
        List<String> pointList = new ArrayList<>();
        // 乱序
        List<String> orderList = new ArrayList<>();

        // 以汉字、数字、字母结尾
        final String END_MATCH = ".*[a-zA-Z0-9_\\u4e00-\\u9fa5]$";

        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            char[] chars = s.toCharArray();
            sb.setLength(0);
            int length = chars.length;
            for (int i = 0; i < length; i++) {
                char aChar = chars[i];
                // 当前下标的数据
                String s1 = new String(new char[]{aChar});
                // 是字母或者数字，拼接字符串
                if (Pattern.matches(END_MATCH, s1)) {
                    sb.append(s1);
                } else {
                    if (sb.length() > 0) {
                        rightList.add(sb.toString());
                        orderList.add(sb.toString());
                        sb.setLength(0);
                    }
                    // 如果符号前面是字母需要在符号列表中加 null
                    if (i > 0 && Pattern.matches(END_MATCH, new String(new char[]{chars[i - 1]}))) {
                        pointList.add(null);
                    }
                    rightList.add(s1);
                    pointList.add(s1);
                }

                // 防止最后一个单词后面没有符号导致最后一个单词不追加到列表中
                if (sb.length() > 0 && i == length - 1) {
                    rightList.add(sb.toString());
                    orderList.add(sb.toString());
                    pointList.add(null);
                    sb.setLength(0);
                }
            }
        }
        System.out.println(rightList);
        System.out.println(pointList);
        System.out.println(orderList);
    }
}
