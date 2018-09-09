package com.zhidejiaoyu.student.service;

import javax.servlet.http.HttpSession;

import com.zhidejiaoyu.common.utils.server.ServerResponse;

public interface DictationService {

	ServerResponse<Object> dictationShow(String unit_id, HttpSession session);

}
