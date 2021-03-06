package com.zhidejiaoyu.student.business.learn.controller;

import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.learn.service.IStudyService;
import com.zhidejiaoyu.student.business.learn.vo.GetVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/learn")
public class LearnNewController extends BaseController {

    @Resource
    private IStudyService wordPictorialService;
    @Resource
    private IStudyService memoryWordService;
    @Resource
    private IStudyService dictationService;
    @Resource
    private IStudyService wordWriteService;
    @Resource
    private IStudyService wordCompletionService;
    @Resource
    private IStudyService sentencePatternTranslationService;
    @Resource
    private IStudyService sentencePatternListeningService;
    @Resource
    private IStudyService sentencePatternWritingService;
    @Resource
    private IStudyService textAuditionService;
    @Resource
    private IStudyService breakThroughTheTextService;
    @Resource
    private IStudyService textTrainingService;
    @Resource
    private IStudyService sentencePatternGameService;

    private final Map<Integer, IStudyService> map = new HashMap<>();

    @PostConstruct
    private void initMap() {
        map.put(1, wordPictorialService);
        map.put(3, memoryWordService);
        map.put(4, dictationService);
        map.put(5, wordWriteService);
        map.put(6, wordCompletionService);
        map.put(7, sentencePatternTranslationService);
        map.put(8, sentencePatternListeningService);
        map.put(10, sentencePatternWritingService);
        map.put(11, textAuditionService);
        map.put(12, textTrainingService);
        map.put(13, breakThroughTheTextService);
        map.put(19, sentencePatternGameService);
        /*
        map.put(14,);

        map.put(16,);
        map.put(17,);
        map.put(18,);
        */
    }

    /**
     * 获取学习内容
     *
     * @param session
     * @param getModel 模块  1，单词图鉴 3，慧记忆 4，会听写
     *                 5，慧默写 6，单词游戏 7，句型翻译 8，句型听力 9，音译练习
     *                 10，句型默写 11，课文试听 12，课文训练 13，闯关测试 14，课文跟读
     *                 15，读语法 16，选语法 17，写语法 18，语法游戏 19，句型游戏
     * @param type     1：普通模式；2：暴走模式
     * @return
     */
    @RequestMapping("/getStudy")
    public Object getStudy(HttpSession session, Integer getModel, Long unitId, Integer type) {
        IStudyService iStudyService = map.get(getModel);
        return iStudyService.getStudy(session, unitId, type);
    }

    /**
     * 保存学习内容
     *
     * @param session
     * @param getModel 模块 1，单词播放机 2，单词图鉴 3，慧记忆 4，会听写
     *                 5，慧默写 6，单词游戏 7，句型翻译 8，句型听力 9，音译练习
     *                 10，句型默写 11，课文试听 12，课文训练 13，闯关测试 14，课文跟读
     *                 15，读语法 16，选语法 17，写语法 18，语法游戏
     * @return
     */
    @RequestMapping("/saveStudy")
    public Object saveStudy(HttpSession session, Integer getModel, GetVo getVo) {
        IStudyService iStudyService = map.get(getModel);
        return iStudyService.saveStudy(session, getVo);
    }


}
