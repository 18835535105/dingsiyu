package com.zhidejiaoyu.student.business.service.simple;

import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

public interface SimpleDictationService {

	ServerResponse<Object> dictationShow(String unit_id, HttpSession session);

}
