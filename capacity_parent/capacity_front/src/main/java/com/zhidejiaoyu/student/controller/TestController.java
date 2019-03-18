package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.Vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.common.Vo.testVo.TestDetailVo;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                                                            @RequestParam(required = false, defaultValue = "false") Boolean isTrue,
                                                            @RequestParam(required = false) String token) {
        Assert.notNull(unitId, "unitId 不能为null");

        Object object = session.getAttribute("token");
        if (object == null || !Objects.equals(object.toString(), token)) {
            return ServerResponse.createBySuccess(new ArrayList<>());
        }

        return StringUtils.isEmpty(studyModel) ? ServerResponse.createByErrorMessage("studyModel can't be null!")
                : testService.getWordUnitTest(session, unitId, studyModel, isTrue);
    }

    /**
     * 获取例句相关单元闯关的测试题
     *
     * @param session
     * @param unitId
     * @param type 1:普通模式；2：暴走模式
     * @return
     */
    @GetMapping("/getSentenceUnitTest")
    public ServerResponse<List<SentenceTranslateVo>> getSentenceUnitTest(HttpSession session, Long unitId,
                                                                         @RequestParam(required = false, defaultValue = "1") Integer type,
                                                                         @RequestParam(required = false, defaultValue = "1") Integer pageNum) {
        if (unitId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "unitId 不能为 null");
        }
        return testService.getSentenceUnitTest(session, unitId, type, pageNum);
    }


    /**
     * 获取例句相关单元闯关的测试题
     * @param unitId
     * @return
     */
    @GetMapping("/gitUnitSentenceTest")
    @ResponseBody
    public ServerResponse<Object> gitUnitSentenceTest(Long unitId) {
        if (unitId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "unitId 不能为 null");
        }
        return testService.gitUnitSentenceTest(unitId);
    }

    /**
     * 保存句型单元闯关测试记录
     *
     * @param session
     * @param wordUnitTestDTO
     * @return
     */
    @PostMapping("/saveTestCenter")
    public ServerResponse saveSentenceUnitTest(HttpSession session, @Valid WordUnitTestDTO wordUnitTestDTO,
                                               BindingResult bindingResult, String testDetail) {
        String msg = ValidateUtil.validate(bindingResult);
        if ("ok".equals(msg)) {
            return testService.saveSentenceUnitTest(session, wordUnitTestDTO, testDetail);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), msg);
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
                                                         BindingResult bindingResult, String testDetail) {
        String msg = ValidateUtil.validate(bindingResult);
        if ("ok".equals(msg)) {
            return testService.saveWordUnitTest(session, wordUnitTestDTO, testDetail);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), msg);
    }




    /**
     * 测试记录首页展示
     *
     * @param course_id 课程id
     * @param type      1:单词测试记录；2：句型测试记录
     */
    @PostMapping("/record")
    public ServerResponse<Object> showRecord(String course_id, @RequestParam(required = false, defaultValue = "1") Integer type,
                                             HttpSession session, Integer page, Integer rows) {
        return testService.showRecord(course_id, type, session, page, rows);
    }

    /**
     * 获取学生测试详情信息
     *
     * @param testId 测试记录id
     * @return
     */
    @GetMapping("/getTestDetail")
    public ServerResponse<TestDetailVo> getTestDetail(HttpSession session, Long testId) {
        if (testId == null) {
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getMsg());
        }
        return testService.getTestDetail(session, testId);
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

    /**
     * 保存句子测试
     * @param session
     * @param wordUnitTestDTO
     * @return
     */
    @PostMapping("/saveCapSentenceTest")
    public ServerResponse saveCapSentenceTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO) {
        if (wordUnitTestDTO.getUnitId() == null) {
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getMsg());
        }
        return testService.saveCapSentenceTest(session, wordUnitTestDTO);
    }

    @PostMapping("/saveCapTeksTest")
    public ServerResponse<Object> saveCapTeksTest(HttpSession session,WordUnitTestDTO wordUnitTestDTO){
        if (wordUnitTestDTO.getUnitId() == null) {
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getMsg());
        }
        return testService.saveCapTeksTest(session, wordUnitTestDTO);
    }

    /**
     * 获取游戏实力初显
     *
     * @param session
     * @return
     */
    @GetMapping("/getStrengthGame")
    public ServerResponse<Map<String, Object>> getStrengthGame(HttpSession session) {
        return testService.getStrengthGame(session);
    }
    /**
     * 获取游戏 找同学
     *
     * @param session
     * @return
     */
    @GetMapping("/getGameTest")
    public ServerResponse<Map<String, Object>> getGameSubject(HttpSession session) {
        return testService.getGameSubject(session);
    }

}
