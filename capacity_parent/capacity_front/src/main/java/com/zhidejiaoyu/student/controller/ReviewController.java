package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.Vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.common.Vo.student.testCenter.TestCenterVo;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.annotation.ControllerLogAnnotation;
import com.zhidejiaoyu.student.service.ReviewService;
import com.zhidejiaoyu.student.vo.TestResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 智能复习/测试复习, 测试中心, 效果测试
 *
 * @author qizhentao
 * @version 1.0
 */
@Slf4j
@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 1. 点击智能复习/测试复习 显示个个模块需要复习的数量
     *
     * @param student_id 学生id
     * @param unit_id    单元id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/number")
    public ServerResponse<Object> testReview(String unit_id, String student_id) {
        Map<String, Integer> map = reviewService.testReview(unit_id, student_id);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 1.2 点击测试复习(慧记忆,慧听写...)
     *
     * @param unit_id  单元id
     * @param classify 类型  1=慧记忆 2=听写 3=默写
     * @return 选择模块所需复习的单词(测试题)
     */
    @ResponseBody
    @RequestMapping(value = "/test")
    public ServerResponse<Object> testCapacityReview(String unit_id, int classify, HttpSession session, boolean pattern) {
        if(classify == 0){
            // 单词图鉴
            return reviewService.testReviewWordPic(unit_id, classify, session, pattern);
        }else {
            return reviewService.testCapacityReview(unit_id, classify, session, pattern);
        }

    }

    /**
     * 获取例句的复习测试题目
     *
     * @param session
     * @param unitId
     * @param classify 4=例句听写 5=例句翻译 6=例句默写
     * @param type     1:普通模式；2：暴走模式
     * @return
     */
    @ResponseBody
    @GetMapping("/getSentenceReviewTest")
    public ServerResponse<List<SentenceTranslateVo>> getSentenceReviewTest(HttpSession session, Long unitId, Integer classify,
                                                                           @RequestParam(required = false, defaultValue = "1") Integer type , boolean pattern) {
        if (unitId == null || classify == null || classify > 6 || classify < 4) {
            return ServerResponse.createByErrorMessage("参数不正确！");
        }
        return reviewService.getSentenceReviewTest(session, unitId, classify, type, pattern);
    }

    /**
     * 保存测试中心测试结果
     *
     * @param correctWord   答对的单词/例句
     * @param errorWord     答错的单词/例句
     * @param correctWordId 答对的单词/例句id
     * @param errorWordId   单错的单词/例句id
     * @param unitId        单元id
     * @param classify      测试模块  0=单词图鉴 1=慧记忆 2=慧听写 3=慧默写 4=例句听力 5=例句翻译 6=例句默写 (五维测试该字段未空)
     * @param courseId      课程id
     * @param session
     * @param point         测试分数
     * @param genre         测试类型：已学测试, 生词测试, 熟词测试, 单词五维测试, 例句五维测试
     * @return 响应信息
     */
    @ResponseBody
    @PostMapping("/saveTestCenter")
    @ControllerLogAnnotation(name = "保存测试中心测试结果")
    public ServerResponse<TestResultVo> saveTestCenter(String[] correctWord, String[] errorWord, Long[] correctWordId, Long[] errorWordId, Long[] unitId,
                                                 Integer classify, Long courseId,HttpSession session, Integer point, String genre,String testDetail) {
        final String wordFiveTest = "单词五维测试";
        final String sentenceFiveTest = "例句五维测试";

        // 当 genre 为空时为其赋值，防止因 genre 为空而导致报错问题
        boolean isWordFiveTest = StringUtils.isEmpty(genre)
                && ((correctWord != null && errorWord != null && (correctWord.length + errorWord.length > 40))
                || (correctWord == null && errorWord.length > 40))
                || (errorWord == null && correctWord.length > 40);
        if (isWordFiveTest) {
            genre = wordFiveTest;
        }
        if (!wordFiveTest.equals(genre) && !sentenceFiveTest.equals(genre)) {
            Assert.notEmpty(unitId, "unitId cant't be null!");
        }
        Assert.notNull(courseId, "courseId can't be null!");
        Assert.notNull(point, "point cant't be null!");
        if (StringUtils.isEmpty(genre)) {
            throw new RuntimeException("genre can't be null!");
        }
        if (wordFiveTest.equals(genre)) {
            classify = 1;
        }
        if (sentenceFiveTest.equals(genre)) {
            classify = 4;
        }
        if (correctWord == null && errorWord == null) {
            return ServerResponse.createByErrorMessage("参数错误！");
        }
        return reviewService.saveTestCenter(correctWord, errorWord, correctWordId, errorWordId, unitId, classify, courseId, session, point, genre,testDetail);
    }

    /**
     * 保存测试复习 测试记录
     *
     * @param correctWord   答对的单词/例句
     * @param errorWord     答错的单词/例句
     * @param correctWordId 答对的单词/例句id
     * @param errorWordId   单错的单词/例句id
     * @param unitId        单元id
     * @param classify      测试模块  1=慧记忆 2=慧听写 3=慧默写 4=例句听力 5=例句翻译 6=例句默写
     * @param courseId      课程id
     * @param session
     * @param point         测试分数
     * @return 响应信息
     */
    @ResponseBody
    @PostMapping("/saveTestReview")
    @ControllerLogAnnotation(name = "保存测试复习测试记录")
    public ServerResponse<TestResultVo> saveTestReview(String[] correctWord, String[] errorWord, Long[] correctWordId,
                                                       Long[] errorWordId, Long[] unitId,
                                                       Integer classify, Long courseId,
                                                       HttpSession session, Integer point) {
        Assert.notEmpty(unitId, "unitId can't be null!");
        Assert.notNull(courseId, "courseId cant't be null!");
        Assert.notNull(point, "point cant't be null!");
        return reviewService.saveTestReview(correctWord, errorWord, correctWordId, errorWordId, unitId, classify, courseId,
                session, point, "复习测试",null);
    }

    /**
     * 保存 智能复习/任务课程 学习记录
     *
     * @param session
     * @param unitId   单词/例句的单元id
     * @param classify 复习模块  1=慧记忆 2=慧听写 3=慧默写 4=例句听力 5=例句翻译 6=例句默写
     * @param word     当前单词或例句
     * @param courseId 复习的课程id
     * @param id       单词/例句的id
     * @param isKnown  是否答对当前例句/单词，true：答对；false：答错
     * @return
     */
    @ResponseBody
    @PostMapping("/saveCapacityReview")
    @ControllerLogAnnotation(name = "保存智能复习")
    public ServerResponse<String> saveCapacityReview(HttpSession session, Long[] unitId, Integer classify, String word,
                                                     Long courseId, Long id, boolean isKnown) {
        if (unitId == null || unitId.length == 0 || courseId == null || classify == null || id == null) {
            return ServerResponse.createByErrorMessage("有必填参数为空!");
        }
        return reviewService.saveCapacityReview(session, unitId, classify, word, courseId, id, isKnown);
    }

    /**
     * url = /capacity
     * 2.1 点击智能复习 - 必要参数单元id (慧记忆,慧听写...)
     *
     * @param unitId   单元id
     * @param classify  类型  1=慧记忆 2=听写 3=默写 4=例句听写 5=例句翻译 6=例句默写
     *                  <p>
     *                  <p>
     *                  url = /taskCourse
     *
     * 2.2 任务课程-点击复习 - 必要参数单元id,课程id
     *
     * @param course_id 课程id
     * @param classify  类型 0=单词图鉴 1=慧记忆 2=慧听写 3=慧默写 4=例句听写 5=例句翻译 6=例句默写
     * @return 4, 5, 6模块所需复习的例句
     * @param type 只针对例句：1：普通模式；2：暴走模式
     * justWord : 正确顺序例句
     * wrongWord : 打乱顺序例句
     * word_Chinese : 翻译
     * readUrl : 读音
     *
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = {"/capacity", "/taskCourse"})
    public Object capacityReview(@RequestParam(value = "unitId",required = false) String unitId,
                                 String course_id, int classify, String judge,
                                                              HttpSession session,
                                                              @RequestParam(required = false, defaultValue = "1") Integer type){
        // 获取学生id
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

        // 图片图鉴模块
        if(classify == 0){
            return reviewService.reviewCapacityPicture(student, unitId, 0, course_id, judge);
        }
        // 慧记忆模块 / 听写 / 默写
        if (classify == 1 || classify == 2 || classify == 3) {
            // 从记忆追踪-...中查询一条记录
            Map<String, Object> map = reviewService.reviewCapacityMemory(student, unitId, classify, course_id);
            return ServerResponse.createBySuccess(map);
        }
        // 例句听写
        if (classify == 4 || classify == 5 || classify == 6) {
            Object object = reviewService.reviewSentenceListen(student, unitId, classify, course_id, type);
            return ServerResponse.createBySuccess(object);
        }

        return ServerResponse.createByErrorMessage("请选择模块! 例慧记忆模块:classify : 1");
    }

    /**
     * 获取智能复习题目，上次登录期间需要复习的单词
     *
     * @param classify   0=单词图鉴 1=慧记忆 2=慧听写 3=慧默写
     * @param session
     * @return
     */
    @ResponseBody
    @GetMapping("/getCapacityReview")
    public ServerResponse<Map<String, Object>> getCapacityReview(Integer classify, HttpSession session) {
        if (classify == 0 || classify == 1 || classify == 2 || classify == 3) {
            return reviewService.getWordReview(session, classify);
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 获取智能复习题目，上次登录期间需要复习的单词
     *
     * @param classify   0=单词图鉴 1=慧记忆 2=慧听写 3=慧默写
     * @param session
     * @return
     */
    @ResponseBody
    @GetMapping("/getAllCapacityReview")
    public ServerResponse<Map<String,Object>> getAllCapacityReview(Integer classify,HttpSession session){
        if (classify == 0 || classify == 1 || classify == 2 || classify == 3) {
            return reviewService.getAllCapacityReview(session, classify);
        }
        return ServerResponse.createBySuccess();
    }

    @ResponseBody
    @GetMapping("/getAllSentenceReview")
    public ServerResponse getAllSentenceReview(Integer classify,HttpSession session){
        if(classify == 4 || classify == 5 || classify == 6){
            return reviewService.getAllSentenceReview(session, classify);
        }
        return ServerResponse.createBySuccess();
    }


    /**
     * 测试中心首页需要的数据
     *
     * @param unitId
     * @param type 1：单词；2：句型
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/testcentreindex")
    public ServerResponse<List<TestCenterVo>> testCentreIndex(Long unitId, @RequestParam(required = false, defaultValue = "1") Integer type,
                                                              HttpSession session) {
        return reviewService.testCentreIndex(unitId, type, session);
    }

    /**
     * 测试中心题
     *
     * @param courseId 课程id
     * @param unitId 单元id
     * @param classify  模块: 0=单词图鉴 1=慧记忆, 2=听写, 3=默写, 4=例句听写, 5=例句翻译, 6=例句默写
     * @param select    选择: 1=已学, 2=生词, 3=熟词
     * @param type 例句相关 1：普通模式；2：暴走模式
     * @return 对应模块选择的题
     */
    @ResponseBody
    @RequestMapping(value = "/testcentre")
    public ServerResponse<Object> testcentre(String courseId, String unitId, int select, int classify, Boolean isTrue,
                                             HttpSession session, @RequestParam(required = false, defaultValue = "1") Integer type) {
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        if(classify == 0){
            // WordPicModel
            return reviewService.testWordPic(courseId, unitId, select, classify, isTrue, session);
        }
        if (classify == 1 || classify == 2 || classify == 3) {
            // 词汇题
            return reviewService.testcentre(courseId, unitId, select, classify, isTrue, session);
        } else {
            // 例句题,
            return ServerResponse.createBySuccess(reviewService.testcentreSentence(unitId, select, classify, session, type));
        }
    }

    /**
     * 五维测试 - 单词模块
     *
     * @param course_id 课程id
     * @param isTrue 默认false第一次点击五维测试 (true扣除1金币)
     *
     * @return 50道题
     */
    @ResponseBody
    @RequestMapping("/fivetest")
    public ServerResponse<Object> fiveDimensionTest(String course_id, boolean isTrue, HttpSession session) {
        return reviewService.fiveDimensionTest(course_id, isTrue, session);
    }

    /**
     * 效果测试 : 每个课程的前30个单词学习完成后出现效果测试。(只前三十个单词出现一次)
     * 题型:英译汉、汉译英、听力理解
     *
     * @param course_id 课程id
     */
    @ResponseBody
    @RequestMapping(value = "/testeffect")
    public ServerResponse<Object> testeffect(String course_id, HttpSession session) {
        return reviewService.testeffect(course_id, session);
    }

}
