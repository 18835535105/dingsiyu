package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.Vo.student.testCenter.TestCenterVo;
import com.zhidejiaoyu.common.pojo.CapacityMemory;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.Vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.student.vo.TestResultVo;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 智能复习业务接口层
 *
 * @author qizhentao
 * @version 1.0
 */
public interface ReviewService extends BaseService<CapacityMemory> {

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
    ServerResponse<Object> testCapacityReview(String unit_id, int classify, HttpSession session, boolean pattern);

    /**
     * 从记忆追踪-...查询一条记录
     *  @param student      学生
     * @param unit_id 单元id*/
    Map<String, Object> reviewCapacityMemory(Student student, String unit_id, int classify, String course_id);

    /**
     * 例句听力
     *
     * @param student    学生
     * @param unit_id
     * @param classify 类型  1=慧记忆 2=听写 3=默写 4=例句听写 5=例句翻译 6=例句默写
     * @param type
     * @return
     */
    Object reviewSentenceListen(Student student, String unit_id, int classify, String course_id, Integer type);

    /**
     * 测试中心页数据展示
     *
     * @param unitId
     * @param type  1：单词；2：句型
     * @param session
     * @return
     */
    ServerResponse<List<TestCenterVo>> testCentreIndex(Long unitId, Integer type, HttpSession session);

    ServerResponse<Object> testcentre(String courseId, String unitId, int select, int classify, Boolean isTrue, HttpSession session);

    List<SentenceTranslateVo> testcentreSentence(String unitId, int select, int classify, HttpSession session, Integer type);

    ServerResponse<Object> testeffect(String course_id, HttpSession session);

    ServerResponse<Object> fiveDimensionTest(String course_id, boolean isTrue, HttpSession session);

    /**
     * 保存测试中心测试结果
     *
     * @param correctWord   答对的单词/例句
     * @param errorWord     答错的单词/例句
     * @param correctWordId 答对的单词/例句id
     * @param errorWordId   单错的单词/例句id
     * @param unitId        单元id
     * @param classify      测试模块  1=慧记忆 2=慧听写 3=慧默写 4=例句听写 5=例句翻译 6=例句默写
     * @param courseId      课程id
     * @param session
     * @param point         测试分数
     * @param genre         测试类型：已学测试, 生词测试, 熟词测试, 五维测试
     * @return 响应信息
     */
    ServerResponse<TestResultVo> saveTestCenter(String[] correctWord, String[] errorWord, Integer[] correctWordId, Integer[] errorWordId, Long[] unitId, Integer classify, Long courseId, HttpSession session, Integer point, String genre,String testDetail);

    /**
     * 保存智能复习/测试复习测试记录
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
     * @param genre         测试类型：效果测试，复习测试
     * @return 响应信息
     */
    ServerResponse<TestResultVo> saveTestReview(String[] correctWord, String[] errorWord, Integer[] correctWordId, Integer[] errorWordId, Long[] unitId, Integer classify, Long courseId, HttpSession session, Integer point, String genre,String testDetail);

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

    ServerResponse<Map<String, Object>> reviewCapacityPicture(Student student, String unitId, int i, String course_id, String judge);

    ServerResponse<Object> testReviewWordPic(String unit_id, int classify, HttpSession session, boolean pattern);

    ServerResponse<Object> testWordPic(String courseId, String unitId, int select, int classify, Boolean isTrue, HttpSession session);

    /**
     * 获取例句的复习测试题目
     *
     * @param session
     * @param unitId
     * @param classify 4=例句听写 5=例句翻译 6=例句默写
     * @param type 1:普通模式；2：暴走模式
     * @return
     */
    ServerResponse<List<SentenceTranslateVo>> getSentenceReviewTest(HttpSession session, Long unitId, Integer classify, Integer type, boolean pattern);

    /**
     * 获取智能复习中单词图鉴需要复习的单词（范围是上次登录期间需要复习的单词）
     *
     * @param session
     * @return
     */
    ServerResponse<Map<String, Object>> getWordReview(HttpSession session, Integer classify);

    ServerResponse<Map<String, Object>> getAllCapacityReview(HttpSession session, Integer classify);

    ServerResponse getAllSentenceReview(HttpSession session, Integer classify);
}
