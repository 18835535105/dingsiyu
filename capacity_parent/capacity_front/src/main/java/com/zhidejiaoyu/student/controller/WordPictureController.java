package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.WordPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.util.resources.ga.LocaleNames_ga;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 单词模块的看图学习
 * @version 1.0
 */
@RestController
@RequestMapping("/picture")
public class WordPictureController {

    @Autowired
    private WordPictureService wordPictureService;

    /**
     * 学习题
     * 三种类型 看词选图,看图选词,听音选图
     *
     * @param session 用于获取当前学生id
     * @return 封装的一道学习题
     */
    @RequestMapping("/getWordPicture")
    public ServerResponse<Object> getWordPicture(HttpSession session, Long courseId, Long unitId){
       return wordPictureService.getWordPicture(session, courseId, unitId);
    }

    /**
     * 单词图鉴单元测试
     *
     * @param session
     * @param unitId 测试的单元
     * @param isTrue 是否确认消费1金币进行测试 true:是；false：否（默认）
     * @return
     */
    @RequestMapping("/getWordPicUnitTest")
    public ServerResponse<Object> getWordPicUnitTest(HttpSession session, Long unitId, Long courseId, Boolean isTrue){
        Assert.notNull(unitId, "parameter: \"unitId\" can not be blank");
        return wordPictureService.getWordPicUnitTest(session, unitId, courseId, isTrue);
    }


}
