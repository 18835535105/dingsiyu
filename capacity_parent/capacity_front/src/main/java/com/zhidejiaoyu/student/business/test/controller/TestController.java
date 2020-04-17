package com.zhidejiaoyu.student.business.test.controller;

import com.zhidejiaoyu.common.dto.WordUnitTestDTO;
import com.zhidejiaoyu.common.dto.phonetic.UnitTestDto;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.utils.ValidateUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.TestResultVo;
import com.zhidejiaoyu.common.vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.common.vo.testVo.TestDetailVo;
import com.zhidejiaoyu.common.vo.testVo.TestResultVO;
import com.zhidejiaoyu.student.business.test.service.TestService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    @Resource
    private TestService testService;


    /**
     * 保存学生游戏测试记录
     *
     * @param session    当前学生session
     * @param testRecord 测试记录
     * @return
     */
    @PostMapping("/saveGameTestRecord")
    public ServerResponse<TestResultVo> saveGameTestRecord(HttpSession session, TestRecord testRecord) {
        return testService.saveGameTestRecord(session, testRecord);
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
    public ServerResponse<List<TestResultVO>> getWordUnitTest(HttpSession session, Long unitId, String studyModel,
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
     * @param type    1:普通模式；2：暴走模式
     * @return
     */
    @GetMapping("/getSentenceUnitTest")
    public ServerResponse<List<SentenceTranslateVo>> getSentenceUnitTest(HttpSession session, Long unitId,
                                                                         @RequestParam(required = false, defaultValue = "1") Integer type,
                                                                         @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                                         Integer studyModel) {
        if (unitId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "unitId 不能为 null");
        }
        return testService.getSentenceUnitTest(session, unitId, type, pageNum, studyModel);
    }


    /**
     * 获取例句相关单元闯关的测试题
     *
     * @param unitId
     * @return
     */
    @GetMapping("/gitUnitSentenceTest")
    @ResponseBody
    public ServerResponse<Object> gitUnitSentenceTest(HttpSession session, Long unitId, Integer type) {
        if (unitId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "unitId 不能为 null");
        }
        return testService.gitUnitSentenceTest(session, unitId, type);
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
     * @param type   1:单词；2：句型；3：课文
     * @return
     */
    @GetMapping("/getTestDetail")
    public ServerResponse<TestDetailVo> getTestDetail(HttpSession session, Long testId, @RequestParam(required = false, defaultValue = "1") Integer type) {
        if (testId == null) {
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getMsg());
        }
        return testService.getTestDetail(session, testId, type);
    }

    /**
     * 学前测试, 课程前测
     *
     * @param session
     * @return
     */
    @PostMapping("getPreSchoolTest")
    public ServerResponse<Object> getPreSchoolTest(HttpSession session) {
        return testService.getPreSchoolTest(session);
    }

    /**
     * 保存句子测试
     *
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

    /**
     * genre 课文测试  闯关测试
     *
     * @param session
     * @param wordUnitTestDTO
     * @return
     */
    @PostMapping("/saveCapTeksTest")
    public ServerResponse<Object> saveCapTeksTest(HttpSession session, WordUnitTestDTO wordUnitTestDTO) {
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
    public ServerResponse<Map<String, Object>> getStrengthGame(HttpSession session, Long unitId) {
        return testService.getStrengthGame(session, unitId);
    }

    /**
     * 获取游戏 找同学
     *
     * @param session
     * @return
     */
    @GetMapping("/getGameTest")
    public ServerResponse<Map<String, Object>> getGameSubject(HttpSession session, Long unitId) {
        return testService.getGameSubject(session, unitId);
    }

    /**
     * 保存单元闯关测试记录
     *
     * @param session
     * @param dto
     * @return
     */
    @PostMapping("/saveUnitTest")
    public ServerResponse saveUnitTest(HttpSession session, @Valid UnitTestDto dto) {
        return testService.savePhoneticSymbolUnitTest(session, dto);
    }

    /**
     * 获取字母单元闯关内容
     */
    @RequestMapping("/getLetterUnitEntry")
    public Object getLetterUnitEntry(HttpSession session, Long unitId) {
        return testService.getLetterUnitEntry(session, unitId);
    }

    /**
     * 保存字母闯关内容
     *
     * @param session
     * @param testRecord
     * @return
     */
    @RequestMapping("/saveLetterUnitEntry")
    public Object saveLetterUnitEntry(HttpSession session, TestRecord testRecord) {
        return testService.saveLetterUnitEntry(session, testRecord);
    }

    /**
     * 获取学后测试内容
     *
     * @param session
     * @param unitId
     * @return
     */
    @RequestMapping("/getLetterAfterLearning")
    public Object getLetterAfterLearning(HttpSession session, Long unitId) {
        return testService.getLetterAfterLearning(session, unitId);
    }

    /**
     * 保存学后测试内容
     *
     * @param session
     * @param testRecord
     * @return
     */
    @RequestMapping("/saveLetterAfterLearning")
    public Object saveLetterAfterLearning(HttpSession session, TestRecord testRecord) {
        return testService.saveLetterAfterLearning(session, testRecord);
    }

    /**
     * 保存阅读测试结果
     *
     * @param session
     * @param testRecord
     * @return
     */
    @RequestMapping("/saveReadTest")
    public Object saveReadTest(HttpSession session, TestRecord testRecord) {
        return testService.saveReadTest(session, testRecord);
    }

}
