package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.pojo.ReadWord;
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
     * @param readWord
     * @return
     */
    @PostMapping("/saveNewWordsBook")
    public ServerResponse<Object> addNewWordsBook(HttpSession session, ReadWord readWord) {
        if (readWord.getWordId() == null || readWord.getCourseId() == null || readWord.getReadTypeId() == null) {
            log.warn("阅读模块添加生词本出错！wordId=[{}], courseId=[{}], readTypeId=[{}]", readWord.getWordId(), readWord.getCourseId(), readWord.getReadTypeId());
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        return readWordService.addNewWordsBook(session, readWord);
    }

    /**
     * 获取生词本列表数据
     *
     * @param session
     * @param courseId 当前课程 id
     * @return
     */
    @GetMapping("/getNewWordsBook")
    public ServerResponse getNewWordsBook(HttpSession session, Long courseId) {
        if (courseId == null) {
            log.error("获取生词本列表数据参数错误！courseId=null");
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        return readWordService.getNewWordsBook(session, courseId);
    }

    /**
     * 单词标红
     *
     * @param session
     * @param readTypeId 阅读类型 id 阅读 A/B
     * @return
     */
    @GetMapping("/markWordRed")
    public ServerResponse markWordRed(HttpSession session, Long courseId, Long readTypeId) {
        if (readTypeId == null) {
            log.error("单词标红参数错误，readTypeId=[null]");
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT);
        }
        return readWordService.markWordRed(session, courseId, readTypeId);
    }
}
