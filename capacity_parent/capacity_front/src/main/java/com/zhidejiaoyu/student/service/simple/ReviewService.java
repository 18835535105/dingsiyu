package com.zhidejiaoyu.student.service.simple;

import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 智能复习业务接口层
 *
 * @author qizhentao
 * @version 1.0
 */
public interface ReviewService {

    /**
     * 根据单元id, 学生id查询智能复习数
     *
     * @param unit_id
     * @param student_id
     * @return
     */
    Map<String, Integer> testReview(String unit_id, String student_id);

    /**
     * 点击测试复习(慧记忆,慧听写...)
     *
     * @param unit_id    单元id
     * @param classify   类型
     * @return 选择模块所需复习的单词(测试题)
     */
    ServerResponse<Object> testCapacityReview(String unit_id, int classify, HttpSession session);

    /**
     * 从记忆追踪-...查询一条记录
     *
     * @param id      学生id
     * @param unit_id 单元id
     */
    Map<String, Object> ReviewCapacity_memory(Long id, String unit_id, int classify, String course_id) throws Exception;

    /**
     * 例句听力
     *
     * @param stuId    学生id
     * @param unit_id
     * @param classify 类型  1=慧记忆 2=听写 3=默写 4=例句听写 5=例句翻译 6=例句默写
     * @return
     */
    Map<String, Object> ReviewSentence_listen(Long stuId, String unit_id, int classify, String course_id);

    ServerResponse<List> testcentreindex(int model, String course_id, HttpSession session);

    ServerResponse<Object> testcentre(String courseId, String unitId, int select, int classify, Boolean isTrue, HttpSession session);

    Map<String, Object> testcentreSentence(String unitId, int select, int classify, HttpSession session);

    ServerResponse<Object> testeffect(String course_id, HttpSession session);

    ServerResponse<Object> fiveDimensionTest(String course_id, boolean isTrue, HttpSession session);

    /**
     * 保存 智能复习 学习记录
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
    ServerResponse<String> saveCapacityReview(HttpSession session, Long[] unitId, Integer classify, String word, Long courseId, Long id, boolean isKnown);

    ServerResponse<Map<String, Object>> Reviewcapacity_picture(Long id, String unitId, int i, String course_id, String judge);

    ServerResponse<Object> testReviewWordPic(String unit_id, int classify, HttpSession session);

    ServerResponse<Object> testWordPic(String courseId, String unitId, int select, int classify, Boolean isTrue, HttpSession session);
}
