package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.Student;
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
     * @return
     */
    ServerResponse getSubjects(HttpSession session, Long unitId, Integer type);

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
     * @param unitId
     * @param wordId
     * @param word
     * @param type
     * @param audio
     * @return
     */
    ServerResponse saveVoice(HttpSession session, Long unitId, Long wordId, String word, Integer type,Integer count, MultipartFile audio);
}
