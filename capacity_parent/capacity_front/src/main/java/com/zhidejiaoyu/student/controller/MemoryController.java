package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.MemoryService;
import com.zhidejiaoyu.student.vo.MemoryStudyVo;
import com.zhidejiaoyu.student.vo.WordIntensifyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 慧记忆模块
 *
 * @author wuchenxi
 * @date 2018年5月10日
 */
@RestController
@RequestMapping("/memory")
public class MemoryController {

    @Autowired
    private MemoryService memoryService;

    /**
     * 获取慧记忆学习的单词信息
     *
     * @param session
     * @param unitId  当前学习的单元id
     * @return
     */
    @GetMapping("/getMemoryWord")
    public ServerResponse<MemoryStudyVo> getMemoryWord(HttpSession session, Long unitId) {
        Assert.notNull(unitId, "unitId cant't be null!");
        return memoryService.getMemoryWord(session, unitId);
    }

    /**
     * 执行此操作后学生可以直接进入学习页面，不需要再次进入学习引导页
     *
     * @param session
     * @param studyModel 学习模块   慧记忆，慧听写，慧默写，例句听力，例句翻译，例句默写
     * @return
     */
    @PostMapping("/clearFirst")
    public ServerResponse<String> clearFirst(HttpSession session, String studyModel) {
        return memoryService.clearFirst(session, studyModel);
    }

    /**
     * 保存慧记忆学习记录 流程： 1.前端先发送保存学生学习信息的请求 2.然后根据后台响应数据再发送获取慧记忆学习的单词信息
     *
     * @param session
     * @param learn
     * @param isKnown 单词会与否，true：熟悉，保存为熟词；false：不熟悉，保存为生词
     * @param plan    当前学习进度
     * @param total   单元单词总数
     * @return
     */
    @PostMapping("/saveMemoryWord")
    public ServerResponse<String> saveMemoryWord(HttpSession session, Learn learn, Boolean isKnown, Integer plan,
                                                 Integer total) {
        return memoryService.saveMemoryWord(session, learn, isKnown, plan, total);
    }

    /**
     * 获取词义强化相关单词
     *
     * @param session
     * @param plan      当前学习进度
     * @param unitId
     * @param wordCount 需要强化的单词个数，默认是10个，如果不足10个达到单元最大单词量，则数量为最新学习的不足10个的单词
     * @return
     */
    @GetMapping("/getWordIntensify")
    public ServerResponse<WordIntensifyVo> getWordIntensify(HttpSession session,
                                                            @RequestParam(defaultValue = "1") Integer plan, Long unitId,
                                                            @RequestParam(defaultValue = "10") Integer wordCount) {
        return memoryService.getWordIntensify(session, plan, unitId, wordCount);
    }

    /**
     * 保存词义强化学习记录
     *
     * @param session
     * @param unitId  单元id
     * @param wordId  单词id
     * @param isTrue  当前单词是否答对
     * @param isLast  本单元单词是否已经学完。true：已经学完，待词义强化完成后进入单元测试；false（默认）：单元内单词还没有学完
     * @return
     */
    @PostMapping("/saveWordIntensify")
    public ServerResponse<String> saveWordIntensify(HttpSession session, Long unitId, Long wordId, Boolean isTrue, Boolean isLast) {
        return memoryService.saveWordIntensify(session, unitId, wordId, isTrue, isLast);
    }

    /**
     * 获取今日学习效率, 在线时长, 有效时长
     */
    @GetMapping("/todayTime")
    public ServerResponse<Object> todayTime(HttpSession session) {
        return memoryService.todayTime(session);
    }
}
