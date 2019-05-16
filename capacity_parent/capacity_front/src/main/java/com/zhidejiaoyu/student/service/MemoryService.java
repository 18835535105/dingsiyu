package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.Vocabulary;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.vo.MemoryStudyVo;
import com.zhidejiaoyu.student.vo.WordIntensifyVo;

import javax.servlet.http.HttpSession;

/**
 * 慧记忆模块service
 *
 * @author wuchenxi
 * @date 2018年5月10日
 */
public interface MemoryService extends BaseService<Vocabulary> {

	/**
	 * 获取慧记忆学习的单词信息
	 *
	 * @param session
	 * @param unitId
	 *            当前学习的单元id
	 * @return
	 */
	Object getMemoryWord(HttpSession session, Long unitId);

	/**
	 * 保存慧记忆学习记录 流程： 1.前端先发送保存学生学习信息的请求 2.然后根据后台响应数据再发送获取慧记忆学习的单词信息
	 *
	 * @param session
	 * @param learn
	 * @param right
	 *            单词会与否，true：熟悉，保存为熟词；false：不熟悉，保存为生词
	 * @param plan
	 *            当前学习进度
	 * @param total
	 *            单元单词总数
	 * @return
	 */
	ServerResponse<String> saveMemoryWord(HttpSession session, Learn learn, Boolean right, Integer plan, Integer total);

	/**
	 * 获取词义强化相关单词
	 *
	 * @param session
	 * @param plan
	 *            当前学习进度
	 * @param unitId
	 * @param wordCount
	 *            需要强化的单词个数，默认是10个，如果不足10个达到单元最大单词量，则数量为最新学习的不足10个的单词
	 * @return
	 */
	ServerResponse<WordIntensifyVo> getWordIntensify(HttpSession session, Integer plan, Long unitId, Integer wordCount);

	/**
	 * 执行此操作后学生可以直接进入学习页面，不需要再次进入学习引导页
	 *
	 * @param session
	 * @param studyModel 学习模块
	 * @return
	 */
    ServerResponse<String> clearFirst(HttpSession session, String studyModel);

	ServerResponse<Object> todayTime(HttpSession session);
}
