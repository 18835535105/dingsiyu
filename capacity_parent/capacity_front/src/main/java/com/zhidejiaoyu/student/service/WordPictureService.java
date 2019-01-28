package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

public interface WordPictureService {

    ServerResponse<Object> getWordPicture(HttpSession session, Long courseId, Long unitId, Integer plan);

    ServerResponse<Object> getWordPicUnitTest(HttpSession session, Long unitId, Long courseId, Boolean isTrue);
}
