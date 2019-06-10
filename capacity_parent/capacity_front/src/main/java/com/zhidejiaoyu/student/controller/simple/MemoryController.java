package com.zhidejiaoyu.student.controller.simple;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.MemoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

/**
 * 九大学习模块
 *
 * @author wuchenxi
 * @date 2018年5月10日
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/memory")
public class MemoryController {

    @Autowired
    private MemoryService memoryService;

    /**
     * 获取个个模块的学习题 - 精简版
     *  1 2 3 4 6 8 9模块需要测试
     *
     * @param session
     * @param type 1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;
     * @param courseId 当前学习的课程id
     * @param unitId  当前学习的单元id
     * @param falg true判断是否需要单元前测, flase不判断
     *
     * @return  301弹框强制测试,  300选择性测试,   data
     */
    @PostMapping("/getMemoryWord")
    public Object getMemoryWord(HttpSession session, int type, Long courseId, Long unitId, boolean falg, boolean anew) {
        return memoryService.getMemoryWord(session, type, courseId, unitId, falg, anew);
    }

    /**
     * 保存学习记录 流程： 1.前端先发送保存学生学习信息的请求 2.然后根据后台响应数据再发送获取慧记忆学习的单词信息 - 精简版
     *
     * @param session
     * @param learn
     * @param isKnown 单词会与否，true：熟悉，保存为熟词；false：不熟悉，保存为生词
     * @param plan    当前学习进度 ./
     * @param total   单元单词总数 /.
     * @param type 1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;
     * @return
     */
    @PostMapping("/saveMemoryWord")
    public ServerResponse<String> saveMemoryWord(HttpSession session, Learn learn, Boolean isKnown, Integer plan,
                                                 Integer total, @NotNull Integer type) {
        if (learn.getVocabularyId() == null) {
            Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
            log.error("保存单词信息时单词id=null, studentId=[{}], learn=[{}], type=[{}]", student.getId(), learn, type);
            return ServerResponse.createBySuccess("ok");
        }
        return memoryService.saveMemoryWord(session, learn, isKnown, plan, total, type);
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
     * 获取今日学习效率, 在线时长, 有效时长
     */
    @GetMapping("/todayTime")
    public ServerResponse<Object> todayTime(HttpSession session) {
        return memoryService.todayTime(session);
    }
}
