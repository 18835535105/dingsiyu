package com.zhidejiaoyu.student.business.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.WordPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

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
     * @param plan 学习进度
     * @return 封装的一道学习题
     */
    @RequestMapping("/getWordPicture")
    public Object getWordPicture(HttpSession session, Long courseId, Long unitId, @RequestParam(defaultValue = "0") Integer plan) {
       return wordPictureService.getWordPicture(session, courseId, unitId, plan);
    }

    /**
     * 单词图鉴单元测试
     *
     * @param session
     * @param unitId 测试的单元
     * @param isTrue 是否确认消费1金币进行测试 true:是；false：否（默认）
     * @param token token 不正确不给学生试题
     * @return
     */
    @RequestMapping("/getWordPicUnitTest")
    public ServerResponse<Object> getWordPicUnitTest(HttpSession session, Long unitId, Long courseId, Boolean isTrue,
                                                     @RequestParam(required = false) String token){
        Assert.notNull(unitId, "parameter: \"unitId\" can not be blank");

//        Object object = session.getAttribute("token");
//        if (object == null || !Objects.equals(object.toString(), token)) {
//            return ServerResponse.createBySuccess(new ArrayList<>());
//        }

        return wordPictureService.getWordPicUnitTest(session, unitId, courseId, isTrue);
    }


}
