package com.zhidejiaoyu.student.business.service.simple;

import com.zhidejiaoyu.common.vo.simple.testVo.TestDetailVo;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.simple.testVo.SimpleTestResultVO;
import com.zhidejiaoyu.common.dto.WordUnitTestDTO;
import com.zhidejiaoyu.common.vo.TestResultVo;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 各种测试service
 *
 * @author wuchenxi
 * @date 2018年5月8日
 */
public interface SimpleTestServiceSimple extends SimpleBaseService<TestRecord> {

	ServerResponse<Object> getPreSchoolTest(HttpSession session, Integer type, Long courseId, Integer typeModel, String studyParagraph, boolean example);

    /**
     * 获取测试题
     *
     * @param session
     * @return
     */
    ServerResponse<Map<String, Object>> getGameSubject(HttpSession session);

    /**
     * 保存学生游戏测试记录，并根据学生游戏测试的成绩推送不同课程或者进入等级测试
     *
     * @param session    当前学生session
     * @param testRecord 测试记录
     * @return
     */
    ServerResponse<Map<String, Object>> saveGameTestRecord(HttpSession session, TestRecord testRecord);

    /**
     * 获取单元闯关的测试题
     *
     * @param session
     * @param unitId
     * @param isTrue    是否确认消费1金币进行测试 true:是；false：否
     * @param example
     * @param model
     * @return
     */
    ServerResponse<List<SimpleTestResultVO>> getWordUnitTest(HttpSession session, Long unitId,
                                                             Boolean isTrue, int type, boolean example, Integer model);

    /**
     * 保存单元测试记录，学习记录，更新记忆追踪数据(包括单词和例句单元测试)
     * @param session
     * @param wordUnitTestDTO 需要保存的数据的参数
     * @param testDetail
     */
    ServerResponse<TestResultVo> saveWordUnitTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO, String testDetail);

    /**
     * 获取单元闯关的测试题
     *
     * @param session
     * @param unitId
     * @param studyModel 学习模块（例句听力，例句翻译，例句默写）
     * @param isSure     是否确认消费1金币进行测试 true:是；false：否
     * @return
     */
    ServerResponse<Map<String, Object>> getSentenceUnitTest(HttpSession session, Long unitId, String studyModel,
                                                            Boolean isSure);

    ServerResponse<Object> showRecord(String course_id, HttpSession session, Integer page, Integer rows);

	ServerResponse<TestResultVo> savePreSchoolTest(HttpSession session, TestRecord testRecord, int type, int typeModel, String testDetail);

	ServerResponse<Object> getTestPaper(long courseId, int typeOne, int typeTwo, String[] unitId);

	/**
	 * 根据课程查询学生需要复习的数据
	 *
	 * @param session
	 * @param courseId
	 * @return
	 */
	ServerResponse<Object> getTaskCourseTest(HttpSession session, Long courseId, int type);

	/**
	 * 保存学生课程复习记录
	 *
	 * @param learn
	 * @param isKnown
	 * @param type
	 * @return
	 */
	ServerResponse<Object> postTaskCourseTest(Learn learn, boolean isKnown, Integer type, HttpSession session);

	/**
	 * 获取学生测试详情信息
	 *
	 * @param session
	 * @param testId  测试记录id
	 * @return
	 */
	ServerResponse<TestDetailVo> getTestDetail(HttpSession session, Long testId);

    Object skipTest(Integer courseId, Integer unitId, Integer type, Integer model, HttpSession session);
}
