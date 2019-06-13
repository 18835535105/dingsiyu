package com.zhidejiaoyu.student.service;

import javax.servlet.http.HttpSession;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 消息中心
 * 
 * @author qizhentao
 * @version 1.0
 */
public interface PersonalCentreService extends BaseService<Student> {

	ServerResponse<Object> newsCentre(HttpSession session);

	ServerResponse<String> newsupdate(HttpSession session, Integer state, Integer[] id);

	ServerResponse<Object> personalIndex(HttpSession session);

	/**
	 * 消息中心-每周时长页面信息 
	 *
	 * @param session
	 * @return
	 */
	ServerResponse<Object> weekDurationIndex(HttpSession session);
	
	/**
	 * 我的报告
	 *  2.每周学习量
	 */
	ServerResponse<Object> weekQuantity(HttpSession session);

	/**
	 * 我的报告
	 *  3.课程统计
	 */
	ServerResponse<Object> CourseStatistics(HttpSession session, int page, int rows);

	/**
	 * 我的报告
	 * 	3.课程统计
	 * 	点击某个课程某个模块下的某个单元 显示 已学/单词总量
	 *
	 * @param session
	 * @param courseId 课程id
	 * @param model 模块: 1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写
	 * @param unitNumber 第几个单元 
	 * @return
	 */
	ServerResponse<Object> courseStatisticsCount(HttpSession session, Integer courseId, Integer model,
			Integer unitNumber);

	/**
	 * 我的排名
	 *  本班排行
	 *  默认金币倒叙排行
	 *  @param rows 
	 *  @param page 
	 *  @param gold 金币 1=正序 2=倒叙  - 默认金币倒叙排行
	 *  @param badge 徽章 1=正序 2=倒叙
	 *  @param certificate 证书 1=正序 2=倒叙
	 *  @param worship 膜拜 1=正序 2=倒叙
	 *  @param model
	 */
	ServerResponse<Object> classSeniority(HttpSession session, Integer page, Integer rows, String gold, String badge, String certificate, String worship, String model, Integer queryType);

	/**
	 * 我的证书
	 *
	 * @param session
	 * @param model 默认0全部显示, 点击的那个模块(7个模块) 1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写；7：五维测试;
	 * @param type 1:牛人证书；2：课程证书
	 * @return
	 */
	ServerResponse<Object> showCcie(HttpSession session, Integer model, Integer type);

	ServerResponse<Object> weekDurationIndexPage(HttpSession session, int page, int rows, Integer year);

	ServerResponse<Object> weekQuantityPage(HttpSession session, int page, int rows, Integer year);

	ServerResponse<Object> durationSeniority(HttpSession session, Integer model, Integer haveUnit, Integer haveTest, Integer haveTime, Integer page, Integer rows);

    ServerResponse<Object> courseStatisticsCountTrue(HttpSession session, Integer unitId, Integer model);

	ServerResponse<Object> getYear(long studentId);

    ServerResponse<Object> payCardIndex(long studentId);

	ServerResponse<Object> postPayCard(long studentId, String card) throws ParseException;

	ServerResponse<Object> getPayCard(long studentId, int page, int rows);

	/**
	 * 将证书的是否已读状态更新为“已读”状态
	 *
	 * @param session
	 * @return
	 */
	ServerResponse updateCcie(HttpSession session);

	/**
	 * 获取当前学生所在班级最新获取勋章的学生信息
	 *
	 * @param session
	 * @return
	 */
	ServerResponse<Object> getMedalInClass(HttpSession session);

    Object getLucky(Integer studentId,HttpSession session);

    ServerResponse<Object> needViewCount(HttpSession session);
}
