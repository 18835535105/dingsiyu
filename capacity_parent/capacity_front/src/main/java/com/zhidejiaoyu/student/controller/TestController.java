package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.Vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.utils.ValidateUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.testUtil.TestResult;
import com.zhidejiaoyu.student.dto.WordUnitTestDTO;
import com.zhidejiaoyu.student.service.TestService;
import com.zhidejiaoyu.student.vo.TestResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 各种测试相关的controller
 *
 * @author wuchenxi
 * @date 2018年5月8日
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    /**
     * 获取游戏测试题目
     *
     * @param session
     * @return
     */
    @GetMapping("/getGameTest")
    public ServerResponse<Map<String, Object>> getGameSubject(HttpSession session) {
        return testService.getGameSubject(session);
    }

    /**
     * 保存学生游戏测试记录
     *
     * @param session    当前学生session
     * @param testRecord 测试记录
     * @return
     */
    @PostMapping("/saveGameTestRecord")
    public ServerResponse<Map<String, Object>> saveGameTestRecord(HttpSession session,  TestRecord testRecord) {
        return testService.saveGameTestRecord(session, testRecord);
    }

    /**
     * 游戏测试推送课程
     *
     * @param session
     * @param point   游戏测试得分
     * @return
     */
    @PostMapping("/pushGameCourse")
    public ServerResponse<String> pushGameCourse(HttpSession session, Integer point) {
        if (point == null) {
            return ServerResponse.createByErrorMessage("point can't be null!");
        }
        return testService.pushGameCourse(session, point);
    }

    /**
     * 获取等级测试测试题
     *
     * @param session
     * @return
     */
    @GetMapping("/getLevelTest")
    public ServerResponse<Map<String, Object>> getLevelTest(HttpSession session) {
        return testService.getLevelTest(session);
    }

    /**
     * 保存学生等级测试记录
     *
     * @param session    当前学生session
     * @param testRecord 测试记录
     * @return
     */
    @PostMapping("/saveLevelTest")
    public ServerResponse<TestResultVo> saveLevelTest(HttpSession session, TestRecord testRecord) {
        return testService.saveLevelTest(session, testRecord);
    }

    /**
     * 获取单词相关单元闯关的测试题
     *
     * @param session
     * @param unitId
     * @param studyModel 学习模块（慧记忆，慧听写，慧默写）
     * @param isTrue     是否确认消费1金币进行测试 true:是；false：否
     * @return
     */
    @GetMapping("/getWordUnitTest")
    public ServerResponse<List<TestResult>> getWordUnitTest(HttpSession session, Long unitId, String studyModel,
                                                            @RequestParam(required = false, defaultValue = "false") Boolean isTrue) {
        Assert.notNull(unitId, "unitId 不能为null");
        return StringUtils.isEmpty(studyModel) ? ServerResponse.createByErrorMessage("studyModel can't be null!")
                : testService.getWordUnitTest(session, unitId, studyModel, isTrue);
    }

    /**
     * 获取例句相关单元闯关的测试题
     *
     * @param session
     * @param unitId
     * @param studyModel 学习模块（4=例句听力，5=例句翻译，6=例句默写）
     * @param isTrue     是否确认消费1金币进行测试 true:是；false：否
     * @param type 1:普通模式；2：暴走模式
     * @return
     */
    @GetMapping("/getSentenceUnitTest")
    public ServerResponse<List<SentenceTranslateVo>> getSentenceUnitTest(HttpSession session, Long unitId, Integer studyModel,
                                                                         @RequestParam(required = false, defaultValue = "false") Boolean isTrue,
                                                                         @RequestParam(required = false, defaultValue = "1") Integer type) {
        if (unitId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "unitId 不能为 null");
        }
        if (StringUtils.isEmpty(studyModel)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "unitId 不能为空");
        }
        return testService.getSentenceUnitTest(session, unitId, studyModel, isTrue, type);
    }

    /**
     * 保存单元测试记录，学习记录，更新记忆追踪数据（包括单词和例句单元测试）
     *
     * @param session
     * @param wordUnitTestDTO 需要保存的数据的参数
     * @return
     */
    @PostMapping("/saveWordUnitTest")
    public ServerResponse<TestResultVo> saveWordUnitTest(HttpSession session, @Valid WordUnitTestDTO wordUnitTestDTO,
                                                         BindingResult bindingResult) {
        String msg = ValidateUtil.validate(bindingResult);
        if ("ok".equals(msg)) {
            return testService.saveWordUnitTest(session, wordUnitTestDTO);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), msg);
    }

    /**
     * 测试记录首页展示
     *
     * @param course_id 课程id
     */
    @PostMapping("/record")
    public ServerResponse<Object> showRecord(String course_id, HttpSession session, Integer page, Integer rows) {
        return testService.showRecord(course_id, session, page, rows);
    }

    /**
     * 学前测试, 课程前测
     *
     * @param session
     * @return
     */
    @PostMapping("getPreSchoolTest")
    public ServerResponse<Object> getPreSchoolTest(HttpSession session){
        return testService.getPreSchoolTest(session);
    }

    /**
     * 保存学前测试
     *
     * @param session    当前学生session
     * @param testRecord 测试记录
     * @return
     */
    @PostMapping("/savePreSchoolTest")
    public ServerResponse<TestResultVo> savePreSchoolTest(HttpSession session, TestRecord testRecord) {
        return testService.savePreSchoolTest(session, testRecord);
    }
}
