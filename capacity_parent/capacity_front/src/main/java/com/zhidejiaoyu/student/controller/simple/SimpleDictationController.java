package com.zhidejiaoyu.student.controller.simple;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.SimpleDictationService;
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
@RequestMapping("/api/dictation")
public class SimpleDictationController {

	@Autowired
	private SimpleDictationService dictationService;

	/**
	 * 出一道题
	 *
	 * @param unit_id 单元id
	 * @return 一道题, 学习进度, 生词, 熟词, 复习
	 */
	@RequestMapping(value = "/dictation")
	public ServerResponse<Object> dictationShow(String unit_id, HttpSession session) {
		return dictationService.dictationShow(unit_id, session);
	}

}
