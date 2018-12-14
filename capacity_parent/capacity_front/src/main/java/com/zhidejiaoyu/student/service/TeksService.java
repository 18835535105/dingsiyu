package com.zhidejiaoyu.student.service;

import com.zhidejiaoyu.common.Vo.student.sentence.CourseUnitVo;
import com.zhidejiaoyu.common.pojo.Teks;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface TeksService extends BaseService<Teks> {

    ServerResponse<List<Teks>> selTeksByUnitId(Integer unitId);


    ServerResponse<Object> selChooseTeks(Integer unitId);

    /**
     * 获取课文的课程及单元信息
     *
     * @param session
     * @return
     */
    ServerResponse<List<CourseUnitVo>> getCourseAndUnit(HttpSession session);
}
