package com.zhidejiaoyu.student.business.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Voice;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

/**
 * 好声音
 *
 * @author wuchenxi
 * @date 2018/8/29
 */
public interface GoodVoiceService extends BaseService<Student> {

    /**
     * 获取好声音题目
     *
     * @param session
     * @param unitId
     * @param type 1：单词；2：例句
     * @param flag  1:获取流程内单词好声音题目；2：获取流程外单词好声音题目
     * @return
     */
    ServerResponse getSubjects(HttpSession session, Long unitId, Integer type, Integer flag);

    /**
     * 获取当前单词或例句的好声音排行信息
     *
     * @param session
     * @param unitId
     * @param wordId
     * @param type
     * @return
     */
    ServerResponse getRank(HttpSession session, Long unitId, Long wordId, Integer type);

    /**
     * 保存录音
     * @param session
     * @param voice
     * @param audio
     * @param type 1：单词评测；2：句子评测
     * @return
     */
    ServerResponse saveVoice(HttpSession session, Voice voice, String word, MultipartFile audio,Integer type);
}
