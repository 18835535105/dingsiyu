package com.zhidejiaoyu.student.controller.simple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.SimpleReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
@Controller
@RequestMapping("/api/review")
public class SimpleReviewController {

    @Autowired
    private SimpleReviewService simpleReviewService;

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
        Map<String, Integer> map = simpleReviewService.testReview(unit_id, student_id);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * 1.2 点击测试复习(慧记忆,慧听写...)
     *
     * @param unit_id  单元id
     * @param classify 类型  1=慧记忆 2=听写 3=默写 4=例句听写 5=例句翻译 6=例句默写
     * @return 选择模块所需复习的单词(测试题)
     * @throws JsonProcessingException
     */
    @ResponseBody
    @RequestMapping(value = "/test")
    public ServerResponse<Object> testCapacityReview(String unit_id, int classify, HttpSession session) throws JsonProcessingException {
        if(classify == 0){
            // 单词图鉴
            return simpleReviewService.testReviewWordPic(unit_id, classify, session);
        }else {
            return simpleReviewService.testCapacityReview(unit_id, classify, session);
        }

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
    public ServerResponse<String> saveCapacityReview(HttpSession session, Long[] unitId, Integer classify, String word,
                                                     Long courseId, Long id, boolean isKnown) {
        if (unitId == null || unitId.length == 0 || courseId == null || classify == null || id == null) {
            return ServerResponse.createByErrorMessage("有必填参数为空!");
        }
        return simpleReviewService.saveCapacityReview(session, unitId, classify, word, courseId, id, isKnown);
    }

    /**
     * 1.4 测试记录(测试结果保存测试记录中)
     *
     * @Param classifyName 测试类型名
     * @Param testDateTime 测试日期时间
     * @Param score 得分
     * @Param quantity 题数量
     */


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
     * justWord : 正确顺序例句
     * wrongWord : 打乱顺序例句
     * word_Chinese : 翻译
     * readUrl : 读音
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = {"/capacity", "/taskCourse"})
    public ServerResponse<Map<String, Object>> CapacityReview(String unitId, String course_id, int classify, String judge, HttpSession session) throws Exception {
        // 获取学生id
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        Long id = student.getId();
        // 图片图鉴模块
        if(classify == 0){
            return simpleReviewService.Reviewcapacity_picture(id, unitId, 0, course_id, judge);
        }
        // 慧记忆模块 / 听写 / 默写
        if (classify == 1 || classify == 2 || classify == 3) {
            // 从记忆追踪-...中查询一条记录
            Map<String, Object> map = simpleReviewService.ReviewCapacity_memory(id, unitId, classify, course_id);
            return ServerResponse.createBySuccess(map);
        }
        // 例句听写
        if (classify == 4 || classify == 5 || classify == 6) {
            Map<String, Object> map = simpleReviewService.ReviewSentence_listen(id, unitId, classify, course_id);
            return ServerResponse.createBySuccess(map);
        }

        return ServerResponse.createByErrorMessage("请选择模块! 例慧记忆模块:classify : 1");
    }

    /**
     * 测试中心首页需要的数据
     *
     * @param model 1=单词模块;2=例句模块
     * @param unitId 改为单元id    !!!!!!!!!!!!!!!!!!!!!!!!
     * @return 六个模块可以测试数量
     */
    @ResponseBody
    @RequestMapping(value = "/testcentreindex")
    public ServerResponse<List> testcentreindex(int model, String unitId, HttpSession session) {
        return simpleReviewService.testcentreindex(model, unitId, session);
    }

    /**
     * 测试中心题
     *
     * @param courseId 课程id
     * @param unitId 单元id
     * @param classify  模块: 0=单词图鉴 1=慧记忆, 2=听写, 3=默写, 4=例句听写, 5=例句翻译, 6=例句默写
     * @param select    选择: 1=已学, 2=生词, 3=熟词
     * @return 对应模块选择的题
     */
    @ResponseBody
    @RequestMapping(value = "/testcentre")
    public ServerResponse<Object> testcentre(String courseId, String unitId, int select, int classify, Boolean isTrue, HttpSession session) {
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        if(classify == 0){
            // WordPicModel
            return simpleReviewService.testWordPic(courseId, unitId, select, classify, isTrue, session);
        }
        if (classify == 1 || classify == 2 || classify == 3) {
            // 词汇题
            return simpleReviewService.testcentre(courseId, unitId, select, classify, isTrue, session);
        } else {
            // 例句题,
            return ServerResponse.createBySuccess(simpleReviewService.testcentreSentence(unitId, select, classify, session));
        }
    }

    /**
     * 五维测试 - 单词模块 - 智能版
     *
     *	取题范围 小学,初中,高中
     *	取题量 100
     *	类型 英译汉,汉译英,听力理解
     *
     * @param course_id 课程id
     * @param isTrue 默认false第一次点击五维测试 (true扣除1金币)
     *
     * @return 50道题
     */
    @ResponseBody
    @RequestMapping("/fivetest")
    public ServerResponse<Object> fiveDimensionTest(String course_id, boolean isTrue, HttpSession session) {
        return simpleReviewService.fiveDimensionTest(course_id, isTrue, session);
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
        return simpleReviewService.testeffect(course_id, session);
    }

}
