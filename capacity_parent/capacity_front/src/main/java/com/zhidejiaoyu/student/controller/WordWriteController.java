package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.WordWriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 慧默写模块
 *
 * @author wuchenxi
 * @date 2018年5月17日 下午4:44:11
 */
@RestController
@RequestMapping("/wordWrite")
public class WordWriteController {

    @Autowired
    private WordWriteService wordWriteService;


    /**
     * 获取慧默写需要学习的单词
     *
     * @param session
     * @param unitId
     * @param ignoreWordId 获取单词时需要忽略的单词 id
     * @return
     */
    @RequestMapping("/getWriteWord")
    public Object getWriteWord(HttpSession session, Long unitId, Long[] ignoreWordId) {
        return wordWriteService.getWriteWord(session, unitId, ignoreWordId);
    }

    /**
     * 保存慧默写学习记录 流程： 1.前端先发送保存学生学习信息的请求 2.然后根据后台响应数据再发送获取慧记忆学习的单词信息
     * 	默写模块错过三次在记忆时间上再加长三小时
     * @param session
     * @param learn
     * @param isKnown  单词会与否，true：熟悉，保存为熟词；false：不熟悉，保存为生词
     * @param plan     当前学习进度
     * @param total    单元单词总数
     * @param classify 保存那个模块的学习记录: 慧听写=2 慧默写=3 单词图鉴=0
     * @return
     */
    @PostMapping("/saveWriteWord")
    public ServerResponse<String> saveWriteWord(HttpSession session, Learn learn, Boolean isKnown, Integer plan,
                                                Integer total, Integer classify) {
        if (plan == null) {
            return ServerResponse.createByErrorMessage("plan 参数非法!");
        }
        if (classify == null) {
            return ServerResponse.createByErrorMessage("classify 参数非法！");
        }
        if (total == null) {
            return ServerResponse.createByErrorMessage("total 参数非法！");
        }
        return wordWriteService.saveWriteWord(session, learn, isKnown, plan, total, classify);
    }

}
