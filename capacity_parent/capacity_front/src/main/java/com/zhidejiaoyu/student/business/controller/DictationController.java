package com.zhidejiaoyu.student.business.controller;

import com.zhidejiaoyu.student.business.service.DictationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 智能听写
 *	记录单元学习进度
 *	1、听写、对比、抄写2、获取指定单元的单词
 *
 *  	1. 一次一道题(首先出超出黄金记忆点的单词)
 *  	2. 一道题完成后,后台接收前台回答的对错, (并保存到learn表中保存该单词是熟词/生词)
 *
 * @author qizhentao
 * @version 1.0
 */
@RestController
@RequestMapping("/dictation")
public class DictationController {

	@Autowired
	private DictationService dictationService;

	/**
	 * 出一道题
	 *
	 * @param unit_id 单元id
	 * @param ignoreWordId 获取单词时需要忽略的单词 id
	 * @return 一道题, 学习进度, 生词, 熟词, 复习
	 */
	@RequestMapping(value = "/dictation")
	public Object dictationShow(String unit_id, HttpSession session, Long[] ignoreWordId) {
		return dictationService.dictationShow(unit_id, session, ignoreWordId);
	}

}
