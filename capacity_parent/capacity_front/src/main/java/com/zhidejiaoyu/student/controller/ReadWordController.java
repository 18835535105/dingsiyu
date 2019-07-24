package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.ReadWordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 生词手册相关
 *
 * @author wuchenxi
 * @date 2019-07-23
 */
@Slf4j
@RestController
@RequestMapping("/newWords")
public class ReadWordController {

    @Autowired
    private ReadWordService readWordService;

    /**
     * 获取选中的单词释义
     *
     * @param word
     * @return
     */
    @GetMapping("/getWordInfo")
    public ServerResponse<Object> getWordInfo(HttpSession session, Long courseId, String word) {
        if (StringUtils.isEmpty(word) || courseId == null) {
            log.error("阅读模块获取指定单词信息出错，参数 word=[{}], courseId=[{}]", word, courseId);
            return ServerResponse.createByError();
        }
        return readWordService.getWordInfo(session, courseId, word);
    }

    /**
     * 添加单词到生词本
     *
     * @param wordId
     * @return
     */
    @PostMapping("/saveNewWordsBook")
    public ServerResponse<Object> addNewWordsBook(HttpSession session, Long courseId, Long wordId) {
        if (wordId == null || courseId == null) {
            log.warn("阅读模块添加生词本出错！wordId=[{}], courseId=[{}]", wordId, courseId);
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        return readWordService.addNewWordsBook(session, courseId, wordId);
    }
}
