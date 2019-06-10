package com.zhidejiaoyu.student.service.simple;

import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;

import javax.servlet.http.HttpSession;

public interface DictationService {

	ServerResponse<Object> dictationShow(String unit_id, HttpSession session);

}
