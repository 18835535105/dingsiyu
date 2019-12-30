package com.zhidejiaoyu.student.business.service;

import com.zhidejiaoyu.common.pojo.Teks;
import com.zhidejiaoyu.common.pojo.TeksNew;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public interface TeksService extends BaseService<Teks> {

    /**
     * 获取所有课文
     *
     * @param unitId
     * @return
     */
    ServerResponse<List<Teks>> selTeksByUnitId(Integer unitId);

    /**
     * 获取课文选词填空
     *
     * @param unitId
     * @param session
     * @return
     */
    ServerResponse<Object> selChooseTeks(Integer unitId, HttpSession session);

    /**
     * 获取课文的课程及单元信息
     *
     * @param session
     * @return
     */
    ServerResponse<Map<String, Object>> getCourseAndUnit(HttpSession session);

    /**
     * 获取默写课文
     *
     * @param unitId
     * @return
     */
    ServerResponse<Object> selWriteTeks(Integer unitId);

    /**
     * 添加得分
     *
     * @param testRecord
     * @param session
     * @return
     */
    ServerResponse<Object> addData(TestRecord testRecord, HttpSession session);

    ServerResponse<Object> selHistoryPronunciation(Integer unitId, HttpSession session);

    ServerResponse<Object> isHistoryPronunciation(Integer unitId, HttpSession session);

    ServerResponse<Object> selSpeakTeksByUnitId(Integer unitId, HttpSession session);

    ServerResponse<Object> selHistoryByCountAndUnitId(Integer count, Integer unitId, HttpSession session);

    ServerResponse<Object> selRankingList(Integer unitId, HttpSession session);

    ServerResponse<Object> getTeksTest(HttpSession session, Integer unitId);

    ServerResponse<Map<String, Object>> saveTeksAudition(HttpSession session, Integer unitId, Integer courseId);

    ServerResponse<List<Map<String, Object>>> getTeksLaterLearnTime(HttpSession session);

    ServerResponse<Map<String, Object>> getIsInto(HttpSession session, Long unitId);

    ServerResponse<Object> getUnitStatus(Long courseId, HttpSession session);

    void getList(String[] split, Map<String, Object> map);
    void getListTeks(List<Object> resultTeks, TeksNew teks);
    ServerResponse<Object> getReturnTestTeks(List<TeksNew> teks, List<TeksNew> addTeks);
    ServerResponse<Object> getTeks(List<TeksNew> teks);
}
