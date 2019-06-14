package com.zhidejiaoyu.student.service.simple;

import com.zhidejiaoyu.common.pojo.Ccie;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 消息中心
 *
 * @author qizhentao
 * @version 1.0
 */
public interface SimplePersonalCentreServiceSimple extends SimpleBaseService<Ccie> {

    ServerResponse<Object> newsCentre(HttpSession session);

    ServerResponse<String> newsUpdate(HttpSession session, Integer state, Integer[] id);

    ServerResponse<Object> personalIndex(HttpSession session);

    /**
     * 消息中心-每周时长页面信息
     *
     * @param session
     * @return
     */
    ServerResponse<Object> weekDurationIndex(HttpSession session);

    /**
     * 我的报告
     * 2.每周学习量
     */
    ServerResponse<Object> weekQuantity(HttpSession session);

    /**
     * 我的报告
     * 3.课程统计
     */
    ServerResponse<Object> courseStatistics(HttpSession session, int page, int rows) throws Exception;

    /**
     * 我的报告
     * 3.课程统计
     * 点击某个课程某个模块下的某个单元 显示 已学/单词总量
     *
     * @param session
     * @param courseId   课程id
     * @param model      模块: 1=慧记忆，2=慧听写，3=慧默写，4=例句听力，5=例句翻译，6=例句默写
     * @param unitNumber 第几个单元
     * @return
     */
    ServerResponse<Object> courseStatisticsCount(HttpSession session, Integer courseId, Integer model,
                                                 Integer unitNumber);

    /**
     * 我的排名
     * 本班排行
     * 默认金币倒叙排行
     *
     * @param rows
     * @param page
     * @param gold        金币 1=正序 2=倒叙  - 默认金币倒叙排行
     * @param badge       徽章 1=正序 2=倒叙
     * @param certificate 证书 1=正序 2=倒叙
     * @param worship     膜拜 1=正序 2=倒叙
     * @param model
     */
    ServerResponse<Object> classSeniority(HttpSession session, Integer page, Integer rows, String gold, String badge, String certificate, String worship, String model, Integer queryType);


    ServerResponse<Object> rankingSeniority(HttpSession session, Integer page, Integer rows, String gold, String badge, String certificate, String worship, String model, Integer queryType);

    /**
     * 默认0全部显示, 点击的那个模块(9个模块) 10:单词辨音; 11:词组辨音; 12:快速单词; 13:快速词组; 14:词汇考点; 15:快速句型;
     * 16:语法辨析; 17单词默写; 18:词组默写;19:能力值测试
     *
     * @param session
     * @param model
     * @return
     */
    ServerResponse<Object> showCcie(HttpSession session, Integer model);

    ServerResponse<Object> weekDurationIndexPage(HttpSession session, int page, int rows, Integer year);

    ServerResponse<Object> durationSeniority(HttpSession session, Integer model, Integer haveUnit, Integer haveTest, Integer haveTime, Integer page, Integer rows);

    ServerResponse<Object> courseStatisticsCountTrue(HttpSession session, Integer unitId, Integer model);

    ServerResponse<Object> getYear(long studentId);

    ServerResponse<Object> payCardIndex(long studentId);

    ServerResponse<Object> postPayCard(long studentId, String card) throws ParseException;

    ServerResponse<Object> getPayCard(long studentId, int page, int rows);

    /**
     * 将证书的是否已读状态更新为“已读”状态
     *
     * @param session
     * @return
     */
    ServerResponse updateCcie(HttpSession session);

    ServerResponse<Object> weekDurationIndexPageTwo(HttpSession session, int page, int rows, Integer yea);

    ServerResponse<Object> weekQuantityPageTwo(HttpSession session, int page, int rows, Integer yea);

    /**
     * 获取当前学生所在班级最新获取勋章的学生信息
     *
     * @param session
     * @return
     */
    ServerResponse<List<Map<String, Object>>> getMedalInClass(HttpSession session);

    /**
     * 更新假数据
     */
    void updateFakeData();

    /**
     * 统计个人中心中需要查看的消息个数
     * <ul>
     * <li>未查看的留言反馈个数</li>
     * <li>可抽奖次数</li>
     * </ul>
     *
     * @param session
     * @return
     */
    ServerResponse needViewCount(HttpSession session);
}
