package com.zhidejiaoyu.student.business.controller.simple;

import com.zhidejiaoyu.common.dto.WordUnitTestDTO;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.utils.ValidateUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.TestResultVo;
import com.zhidejiaoyu.common.vo.simple.testVo.SimpleTestResultVO;
import com.zhidejiaoyu.common.vo.simple.testVo.TestDetailVo;
import com.zhidejiaoyu.student.business.service.simple.SimpleTestServiceSimple;
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
@RequestMapping("/api/test")
public class SimpleTestController {

    @Autowired
    private SimpleTestServiceSimple testService;

    /**
     * 获取学前测试, 学后测试50道题 - 精简版
     * 能力值测试100道题, 取题范围小学 初中 高中, 类型英译汉 汉译英 听力理解 - 精简版
     *
     * @param type           1=学前测试 2=学后测试 3=能力值测试
     * @param typeModel      1=辨音模块 2=慧记忆模块 3=慧默写模块
     * @param studyParagraph 小学, 初中, 高中 - 只能力值测试有该字段
     * @param session
     * @return
     */
    @PostMapping("/getPreSchoolTest")
    public ServerResponse<Object> getPreSchoolTest(HttpSession session, Integer type, Long courseId, Integer typeModel,
                                                   String studyParagraph, boolean example, @RequestParam(required = false) String token) {
        Object object = session.getAttribute("token");
        // 能力值测试可以没有 token
        boolean flag = (object == null || !Objects.equals(object.toString(), token)) && type != 3;
        if (flag) {
            return ServerResponse.createBySuccess(new ArrayList<>());
        }
        return testService.getPreSchoolTest(session, type, courseId, typeModel, studyParagraph, example);
    }

    /**
     * 获取单元前测, 单元闯关的测试题 - 简洁版
     *
     * @param session
     * @param unitId  单元id
     * @param isTrue  是否确认消费1金币进行测试 true:是；false：否
     * @param type    模块: 1:慧辨音 2:慧记忆 3:慧默写
     * @param token   token 不正确不给学生试题
     * @return
     */
    @GetMapping("/getWordUnitTest")
    public ServerResponse<List<SimpleTestResultVO>> getWordUnitTest(HttpSession session, Long unitId,
                                                                    @RequestParam(required = false, defaultValue = "false") Boolean isTrue,
                                                                    int type, boolean example, Integer model, @RequestParam(required = false) String token) {
        Assert.notNull(unitId, "unitId 不能为null");

        Object object = session.getAttribute("token");
        if (object == null || !Objects.equals(object.toString(), token)) {
            return ServerResponse.createBySuccess(new ArrayList<>());
        }

        return testService.getWordUnitTest(session, unitId, isTrue, type, example, model);
    }

    /**
     * 保存学后测试,单元前测,能力值测试        只保存测试记录 - 精简版
     *
     * @param session    当前学生session
     * @param testDetail 测试记录详细信息
     * @param testRecord 测试记录保存
     * @param type       2=学后测试 3=单元前测 4=能力值测试
     * @param typeModel  1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写; 20:能力值测试
     * @return
     */
    @PostMapping("/savePreSchoolTest")
    public ServerResponse<TestResultVo> savePreSchoolTest(HttpSession session, String testDetail,
                                                          TestRecord testRecord, int type, int typeModel) {
        return testService.savePreSchoolTest(session, testRecord, type, typeModel, testDetail);
    }

    /**
     * 保存单元闯关测试- 精简版
     *
     * @param session
     * @param testDetail      测试详细记录
     * @param wordUnitTestDTO 需要保存的数据的参数
     * @return
     */
    @PostMapping("/saveWordUnitTest")
    public ServerResponse<TestResultVo> saveWordUnitTest(HttpSession session, String testDetail, @Valid WordUnitTestDTO wordUnitTestDTO,
                                                         BindingResult bindingResult) {
        String msg = ValidateUtil.validate(bindingResult);
        if ("ok".equals(msg)) {
            return testService.saveWordUnitTest(session, wordUnitTestDTO, testDetail);
        }
        return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), msg);
    }

    /**
     * 生成试卷 - 精简版
     *
     * @param courseId 课程id
     * @param typeOne  取题范围: 1=全部 2=追词纪
     * @param typeTwo  取题范围: 1=较少(20) 2=普通(40) 3=较多(100)
     * @param unitId   单元id null=全部
     * @return
     */
    @RequestMapping("/getTestPaper")
    public ServerResponse<Object> getTestPaper(long courseId, int typeOne, int typeTwo, @RequestParam(value = "unitId") String[] unitId) {
        return testService.getTestPaper(courseId, typeOne, typeTwo, unitId);
    }

    /**
     * 任务课程复习题 - 精简版
     */
    @RequestMapping("/getTaskCourseTest")
    public ServerResponse<Object> getTaskCourseTest(HttpSession session, Long courseId, int type) {
        return testService.getTaskCourseTest(session, courseId, type);
    }

    /**
     * 保存任务课程复习记录 - 精简版
     * <p>
     * |courseId |是  |long |课程id   |
     * |unitId |是  |long | 单元id    |
     * |vocabularyId     |是  |long | 单词id    |
     * |isKnown|是|boolean|是否知道该单词  true:知道；false：不知道|
     * |plan|是|int|当前学习进度|
     * |total|是|int|当前单元单词总数|
     * |type|是|int|1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写|
     */
    @RequestMapping("/postTaskCourseTest")
    public ServerResponse<Object> postTaskCourseTest(Learn learn, boolean isKnown, Integer type, HttpSession session) {
        if (type == null) {
            ServerResponse.createByErrorMessage("type 不能为空！");
        }
        return testService.postTaskCourseTest(learn, isKnown, type, session);
    }

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
    public ServerResponse<Map<String, Object>> saveGameTestRecord(HttpSession session, TestRecord testRecord) {
        return testService.saveGameTestRecord(session, testRecord);
    }

    /**
     * 获取例句相关单元闯关的测试题
     *
     * @param session
     * @param unitId
     * @param studyModel 学习模块（例句听力，例句翻译，例句默写）
     * @param isSure     是否确认消费1金币进行测试 true:是；false：否
     * @return
     */
    @GetMapping("/getSentenceUnitTest")
    public ServerResponse<Map<String, Object>> getSentenceUnitTest(HttpSession session, Long unitId, String studyModel,
                                                                   @RequestParam(required = false, defaultValue = "false") Boolean isSure) {
        if (unitId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "unitId 不能为 null");
        }
        if (StringUtils.isEmpty(studyModel)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "unitId 不能为空");
        }
        return testService.getSentenceUnitTest(session, unitId, studyModel, isSure);
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
     * @param courseId 课程id
     * @param unitId   单元id
     * @param type     1，单元前测   2，单元后测
     * @param model    1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写; 20:能力值测试
     * @param session
     * @return
     */
    //跳过绝招好课单元测试和学后测试
    @PostMapping("/skipTest")
    public Object skipTest(Integer courseId, Integer unitId, Integer type, Integer model, HttpSession session) {
        return testService.skipTest(courseId, unitId, type, model, session);
    }
}
