package com.zhidejiaoyu.student.business.controller;

import com.zhidejiaoyu.student.business.service.IStudyService;
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
    private IStudyService sentencePatternTranslationService;
    private Map<Integer, IStudyService> map = new HashMap<>();

    @PostConstruct
    private void initMap() {
        map.put(1, wordPictorialService);
        map.put(3, memoryWordService);
        map.put(4, dictationService);
        map.put(5, wordWriteService);
        map.put(7, sentencePatternTranslationService);
       /* map.put(8,);
        map.put(9,);
        map.put(10,);
        map.put(11,);
        map.put(12,);
        map.put(13,);
        map.put(14,);
        map.put(15,);
        map.put(16,);
        map.put(17,);
        map.put(18,);*/
    }

    /**
     * 获取学习内容
     *
     * @param session
     * @param getModel 模块  1，单词图鉴 3，慧记忆 4，会听写
     *                 5，慧默写 6，单词游戏 7，句型翻译 8，句型听力 9，音译练习
     *                 10，句型默写 11，课文试听 12，课文训练 13，闯关测试 14，课文跟读
     *                 15，读语法 16，选语法 17，写语法 18，语法游戏
     * @param difficulty  1：普通模式；2：暴走模式
     * @return
     */
    @RequestMapping("/getStudy")
    public Object getStudy(HttpSession session, Integer getModel, Long unitId,Integer difficulty) {
        IStudyService iStudyService = map.get(getModel);
        return iStudyService.getStudy(session, unitId,difficulty);
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
    public Object saveStudy(HttpSession session, Integer getModel,
                            Long unitId, Long courseId, Long wordId,
                            boolean isKnown, Integer plan, Integer total, Long flowId) {
        IStudyService iStudyService = map.get(getModel);
        return iStudyService.saveStudy(session, unitId, wordId, isKnown, plan, total, courseId, flowId);
    }


}
