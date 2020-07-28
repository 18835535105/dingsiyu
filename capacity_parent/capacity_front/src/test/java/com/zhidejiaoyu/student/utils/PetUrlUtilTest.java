package com.zhidejiaoyu.student.utils;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.pet.PetUrlUtil;
import org.junit.Test;

/**
 * 测试获取宠物图片地址
 *
 * @author wuchenxi
 * @date 2018/7/31
 */
public class PetUrlUtilTest {

    private String[] genres = {"摸底测试", "单元闯关测试", "阶段测试", "智能复习测试", "已学测试", "生词测试", "熟词测试", "五维测试"};

    private int[] points = {10, 80, 90, 100};

    private String[] petName = {"大明白", "李糖心", "威士顿", "无名"};

    @Test
    public void getTestPetUrl() {
        for (String genre : genres) {
            for (int i = 0; i < points.length; i++) {
                String url = PetUrlUtil.getTestPetUrl(new Student(petName[i]), points[i], genre, null);
                System.out.println(petName[i] + " : " + points[i] + " : " + genre + " : " + url);
            }

        }
    }
}
