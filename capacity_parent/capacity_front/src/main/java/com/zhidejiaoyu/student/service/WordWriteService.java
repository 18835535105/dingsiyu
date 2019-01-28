package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.vo.WordWriteStudyVo;

import javax.servlet.http.HttpSession;

/**
 * 慧默写模块service
 * 
 * @author wuchenxi
 * @date 2018年5月17日 下午4:51:30
 *
 */
public interface WordWriteService extends BaseService<Vocabulary> {

	/**
	 * 获取慧默写需要学习的单词
	 * 
	 * @param session
	 * @param unitId
	 * @return
	 */
	ServerResponse<WordWriteStudyVo> getWriteWord(HttpSession session, Long unitId);

	/**
	 * 保存慧默写学习记录 流程： 1.前端先发送保存学生学习信息的请求 2.然后根据后台响应数据再发送获取慧记忆学习的单词信息
	 * 
	 * @param session
	 * @param learn
	 * @param isKnown
	 *            单词会与否，true：熟悉，保存为熟词；false：不熟悉，保存为生词
	 * @param plan
	 *            当前学习进度
	 * @param total
	 *            单元单词总数
	 *            @param classify 保存那个模块的学习记录: 慧听写=2 慧默写=3
	 * @return
	 */
	ServerResponse<String> saveWriteWord(HttpSession session, Learn learn, Boolean isKnown, Integer plan,
			Integer total, Integer classify);

}
