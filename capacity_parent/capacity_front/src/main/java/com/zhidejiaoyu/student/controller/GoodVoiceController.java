package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.pojo.Voice;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.annotation.ControllerLogAnnotation;
import com.zhidejiaoyu.student.service.GoodVoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 好声音
 *
 * @author wuchenxi
 * @date 2018/8/29
 */
@RestController
@RequestMapping("/voice")
@Validated
public class GoodVoiceController {

    @Autowired
    private GoodVoiceService goodVoiceService;

    /**
     * 获取好声音题目
     *
     * @param session
     * @param unitId
     * @param type  1：单词；2：例句
     * @param flag  1:获取流程内单词好声音题目；2：获取流程外单词好声音题目
     * @return
     */
    @GetMapping("/getSubjects")
    public ServerResponse getSubjects(HttpSession session, @NotNull(message = "unitId 不能为空！") Long unitId,
                                      @RequestParam(required = false, defaultValue = "1") Integer type,
                                      @RequestParam(required = false, defaultValue = "1") Integer flag) {
        return goodVoiceService.getSubjects(session, unitId, type, flag);
    }

    /**
     * 获取学生排行信息
     *
     * @param session
     * @param unitId
     * @param wordId    当前单词、例句id
     * @param type  1：单词；2：例句
     * @return
     */
    @GetMapping("/getRank")
    public ServerResponse getRank(HttpSession session, @NotNull(message = "unitId 不能为空！") Long unitId,
                                  @NotNull(message = "wordId 不能为空！") Long wordId,
                                  @RequestParam(required = false, defaultValue = "1") Integer type) {
        return goodVoiceService.getRank(session, unitId, wordId, type);
    }

    /**
     * 保存录音
     *
     * @param session
     * @param word 需要评测的文本内容
     * @param audio 录音文件
     * @return
     */
    @PostMapping("/saveVoice")
    @ControllerLogAnnotation(name = "保存单词好声音")
    public ServerResponse saveVoice(HttpSession session, Voice voice,
                                    @NotEmpty(message = "word 不能为空！") String word,
                                    @NotNull(message = "录音不能为空！") MultipartFile audio) {
        voice.setCount(0);
        return goodVoiceService.saveVoice(session, voice, word, audio,1);
    }

    /**
     * 课文好声音
     */
    @PostMapping("/saveTeks")
    @ControllerLogAnnotation(name = "保存课文跟读")
    public ServerResponse saveText(HttpSession session, Voice voice,
                                   @NotNull(message = "wordId 不能为空！") Long sentenceId,
                                   @NotEmpty(message = "word 不能为空！") String sentence,
                                   @NotNull(message = "录音不能为空！") MultipartFile audio){
        voice.setWordId(sentenceId);
        voice.setCount(0);
        return goodVoiceService.saveVoice(session, voice, sentence, audio,2);
    }
}
