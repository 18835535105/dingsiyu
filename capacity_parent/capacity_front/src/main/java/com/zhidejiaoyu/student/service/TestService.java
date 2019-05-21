package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.Vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.common.Vo.testVo.TestDetailVo;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.testUtil.TestResult;
import com.zhidejiaoyu.student.dto.WordUnitTestDTO;
import com.zhidejiaoyu.student.dto.phonetic.UnitTestDto;
import com.zhidejiaoyu.student.vo.TestResultVo;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 各种测试service
 *
 * @author wuchenxi
 * @date 2018年5月8日
 */
public interface TestService extends BaseService<TestRecord> {

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
     * 获取等级测试测试题
     *
     * @param session
     * @return
     */
    ServerResponse<Map<String, Object>> getLevelTest(HttpSession session);

    /**
     * 保存学生等级测试记录
     *
     * @param session    当前学生session
     * @param testRecord 测试记录
     * @return
     */
    ServerResponse<TestResultVo> saveLevelTest(HttpSession session, TestRecord testRecord);

    /**
     * 获取单元闯关的测试题
     *
     * @param session
     * @param unitId
     * @param testModel 学习模块（慧记忆，慧听写，慧默写）
     * @param isTrue    是否确认消费1金币进行测试 true:是；false：否
     * @return
     */
    ServerResponse<List<TestResult>> getWordUnitTest(HttpSession session, Long unitId, String testModel,
                                                     Boolean isTrue);

    /**
     * 保存单元测试记录，学习记录，更新记忆追踪数据(包括单词和例句单元测试)
     * @param session
     * @param wordUnitTestDTO 需要保存的数据的参数
     * @param testDetail
     */
    ServerResponse<TestResultVo> saveWordUnitTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO, String testDetail);

    /**
     * 保存句子测试记录，学习记录，更新记忆追踪数据(包括单词和例句单元测试)
     * @param session
     * @param wordUnitTestDTO 需要保存的数据的参数
     * @param testDetail
     */
    ServerResponse<TestResultVo> saveSentenceUnitTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO, String testDetail);

    /**
     * 获取单元闯关的测试题
     *
     * @param session
     * @param unitId
     * @param type 1:普通模式；2：暴走模式
     * @param pageNum
     * @return
     */
    ServerResponse<List<SentenceTranslateVo>> getSentenceUnitTest(HttpSession session, Long unitId, Integer type, Integer pageNum);

    /**
     * @param course_id
     * @param type      1:单词测试记录；2：句型测试记录
     * @param session
     * @param page
     * @param rows
     * @return
     */
    ServerResponse<Object> showRecord(String course_id, Integer type, HttpSession session, Integer page, Integer rows);


    /**
     * 游戏测试推送课程
     *
     * @param session
     * @param point 游戏测试得分
     * @return
     */
    ServerResponse<String> pushGameCourse(HttpSession session, Integer point);

    ServerResponse<Object> getPreSchoolTest(HttpSession session);

    ServerResponse<TestResultVo> savePreSchoolTest(HttpSession session, TestRecord testRecord);

    /**
     * 获取例句单元测试
     * @param unitId
     * @return
     */
    ServerResponse<Object> gitUnitSentenceTest(Long unitId);

    /**
     * 音译测试（音译测试）
     *
     * @param session
     * @param wordUnitTestDTO
     * @return
     */
    ServerResponse<Object> saveCapSentenceTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO);

    /**
     * 获取学生测试详情信息
     *
     * @param session
     * @param testId  测试记录id
     * @param type
     * @return
     */
    ServerResponse<TestDetailVo> getTestDetail(HttpSession session, Long testId, Integer type);

    ServerResponse<Object> saveCapTeksTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO);

    ServerResponse<Map<String, Object>> getStrengthGame(HttpSession session);

    /**
     * 保存单元闯关测试记录
     *
     * @param session
     * @param unitId
     * @return
     */
    ServerResponse savePhoneticSymbolUnitTest(HttpSession session, UnitTestDto unitId);

    Object getLetterUnitEntry(HttpSession session,Long unitId);

    Object saveLetterUnitEntry(HttpSession session, TestRecord testRecord);

    Object getLetterAfterLearning(HttpSession session, Long unitId);

    Object saveLetterAfterLearning(HttpSession session, TestRecord testRecord);
}
