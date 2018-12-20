package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.Vo.student.sentence.CourseUnitVo;
import com.zhidejiaoyu.common.pojo.Teks;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.List;

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
    ServerResponse<List<CourseUnitVo>> getCourseAndUnit(HttpSession session);

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
}