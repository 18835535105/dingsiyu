package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.GoodVoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
     * @return
     */
    @GetMapping("/getSubjects")
    public ServerResponse getSubjects(HttpSession session, @NotNull(message = "unitId 不能为空！") Long unitId,
                                      @RequestParam(required = false, defaultValue = "1") Integer type) {
        return goodVoiceService.getSubjects(session, unitId, type);
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
     * @param unitId
     * @param wordId
     * @param word 需要评测的文本内容
     * @param type
     * @param audio 录音文件
     * @return
     */
    @PostMapping("/saveVoice")
    public ServerResponse saveVoice(HttpSession session, @NotNull(message = "unitId 不能为空！") Long unitId,
                                    @NotNull(message = "wordId 不能为空！") Long wordId,
                                    @NotEmpty(message = "word 不能为空！") String word,
                                    @NotNull(message = "type 不能为空！") @Min(1) @Max(2) Integer type,
                                    @NotNull(message = "录音不能为空！") MultipartFile audio) {

        return goodVoiceService.saveVoice(session, unitId, wordId, word, type, audio);
    }
}
