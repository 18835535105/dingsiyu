package com.zhidejiaoyu.student.business.service;

import com.zhidejiaoyu.common.pojo.Vocabulary;

import javax.servlet.http.HttpSession;

public interface DictationService extends BaseService<Vocabulary> {

	Object dictationShow(String unit_id, HttpSession session, Long[] ignoreWordId);

}
