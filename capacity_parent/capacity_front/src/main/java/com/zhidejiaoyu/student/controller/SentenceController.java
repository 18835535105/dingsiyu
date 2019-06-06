package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.Vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.annotation.ControllerLogAnnotation;
import com.zhidejiaoyu.student.service.SentenceService;
import com.zhidejiaoyu.student.vo.SentenceWordInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 例句相关controller
 *
 * @author wuchenxi
 * @date 2018/5/21 14:59
 */
@Slf4j
@RestController
@RequestMapping("/sentence")
public class SentenceController {

    @Autowired
    private SentenceService sentenceService;

    /**
     * 获取例句翻译、例句听写、例句默写学习题目
     *
     * @param session
     * @param unitId
     * @param classify 选择的学习模块: 4=例句听力, 5=例句翻译, 6=例句默写
     * @param type     学习模式：（默认）1：普通模式；2：暴走模式
     * @return
     */
    @GetMapping("/getSentenceTranslate")
    public ServerResponse<SentenceTranslateVo> getSentenceTranslate(HttpSession session, Long unitId, Integer classify,
                                                                    @RequestParam(required = false, defaultValue = "1") Integer type) {
        if (unitId == null || classify == null || classify < 4 || classify > 6) {
            return ServerResponse.createByErrorMessage("参数非法！");
        }
        return sentenceService.getSentenceTranslate(session, unitId, classify, type);
    }

    @GetMapping("/getIsInto")
    public ServerResponse<Object> getIsInto(HttpSession session, Long unitId) {

        return sentenceService.getIsInto(session, unitId);
    }

    /**
     * 保存例句翻译、例句听力、例句默写学习数据，包括相关记忆追踪内容
     *
     * @param session
     * @param learn
     * @param isKnown  是否答对    true:答对；false：答错
     * @param plan     进度
     * @param total    单元例句总数
     * @param classify 选择的学习模块: 例句听力, 例句翻译, 例句默写
     * @return
     */
    @PostMapping("/saveSentenceTranslate")
    @ControllerLogAnnotation(name = "保存句型学习记录")
    public ServerResponse<String> saveSentenceTranslate(HttpSession session, Learn learn, Boolean isKnown, Integer plan,
                                                        Integer total, String classify,Integer unitId) {
        if (plan == null || total == null) {
            return ServerResponse.createByErrorMessage("参数非法");
        }
        return sentenceService.saveSentenceTranslate(session, learn, isKnown, plan, total, classify,unitId);
    }

    /**
     * 例句听力和例句翻译时获取指定单词的信息
     *
     * @param session  session信息
     * @param unitId   当前例句的单元id
     * @param courseId 当前例句的课程id
     * @param word     需要查询的单词英文
     * @return ServerResponse<SentenceWordInfoVo>
     */
    @GetMapping("/getSentenceWordInfo")
    public ServerResponse<SentenceWordInfoVo> getSentenceWordInfo(HttpSession session, Long unitId, Long courseId, String word) {
        Assert.notNull(unitId, "unitId 不能为空！");
        Assert.notNull(courseId, "courseId 不能为空");
        Assert.notNull(word, "单词不能为空！");
        return sentenceService.getSentenceWordInfo(session, unitId, courseId, word);
    }

    /**
     * 例句听力和例句翻译时将指定单词保存到生词本中
     *
     * @param session  session信息
     * @param unitId   需要保存的单词所在的单元id
     * @param courseId 需要保存的单词所在的课程id
     * @param wordId   需要保存的单词id
     * @return ServerResponse
     */
    @PostMapping("/saveUnknownWord")
    public ServerResponse<String> saveUnknownWord(HttpSession session, Long unitId, Long courseId, Long wordId) {
        if (unitId == null) {
            log.error("单词添加生词本失败:unitId=null");
            return ServerResponse.createBySuccess();
        }
        if (courseId == null) {
            log.error("单词添加生词本失败:courseId=null");
            return ServerResponse.createBySuccess();
        }
        if (wordId == null) {
            log.error("单词添加生词本失败:wordId=null");
            return ServerResponse.createBySuccess();
        }
        return sentenceService.saveUnknownWord(session, unitId, courseId, wordId);
    }

    /**
     * 进入句型学习页获取学生所有课程及单元，并标记当前学习、未学习、已学习状态
     *
     * @param session
     * @return
     */
    @ResponseBody
    @GetMapping("/getLearnCourseAndUnit")
    @ControllerLogAnnotation(name = "获取句型首页数据")
    public ServerResponse<Object> getLearnCourseAndUnit(HttpSession session) {
        return sentenceService.getLearnCourseAndUnit(session);
    }

    @ResponseBody
    @GetMapping("/getLearnUnitByCourse")
    @ControllerLogAnnotation(name = "获取句型首页数据")
    public ServerResponse<Object> getLearnUnitByCourse(HttpSession session,Long courseId) {
        return sentenceService.getLearnUnitByCourse(session,courseId);
    }


    @ResponseBody
    @GetMapping("/getSentenceLaterLearnTime")
    public ServerResponse<Object> getSentenceLaterLearnTime(HttpSession session){
        return sentenceService.getSentenceLaterLearnTime(session);
    }

    /**
     * 模块重新学习
     * @param session
     * @return
     */
    @ResponseBody
    @GetMapping("/getModuleRelearning")
    public ServerResponse<Object> getModuleRelearning(HttpSession session,String studyModel,Integer unitId){
        return sentenceService.getModuleRelearning(session,studyModel,unitId);
    }




}
