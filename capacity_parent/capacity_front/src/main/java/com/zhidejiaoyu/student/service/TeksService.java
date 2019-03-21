package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.pojo.Teks;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public interface TeksService extends BaseService<Teks> {

    //获取所有课文
    ServerResponse<List<Teks>> selTeksByUnitId(Integer unitId);

    //获取课文选词填空
    ServerResponse<Object> selChooseTeks(Integer unitId,HttpSession session);

    /**
     * 获取课文的课程及单元信息
     *
     * @param session
     * @return
     */
    ServerResponse<Map<String,Object>> getCourseAndUnit(HttpSession session);

    //获取默写课文
    ServerResponse<Object> selWriteTeks(Integer unitId);

    /**
     * 添加得分
     * @param testRecord
     * @param session
     * @return
     */
    ServerResponse<Object> addData(TestRecord testRecord, HttpSession session);

    ServerResponse<Object> selHistoryPronunciation(Integer unitId,HttpSession session);

    ServerResponse<Object> isHistoryPronunciation(Integer unitId, HttpSession session);

    ServerResponse<Object> selSpeakTeksByUnitId(Integer unitId,HttpSession session);

    ServerResponse<Object> selHistoryByCountAndUnitId(Integer count, Integer unitId, HttpSession session);

    ServerResponse<Object> selRankingList(Integer unitId, HttpSession session);

    ServerResponse<Object> getTeksTest(Integer unitId);

    ServerResponse<Map<String, Object>> saveTeksAudition(HttpSession session, Integer unitId, Integer courseId);

    ServerResponse<List<Map<String, Object>>> getTeksLaterLearnTime(HttpSession session);

    ServerResponse<Map<String, Object>> getIsInto(HttpSession session,Long unitId);

}
