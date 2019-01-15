package com.zhidejiaoyu.student.controller;


import com.zhidejiaoyu.common.Vo.student.sentence.CourseUnitVo;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.TeksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teks")
public class TeksController  {


    @Autowired
    private TeksService teksService;

    /**
     * 获取课文的课程及单元信息
     *
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTextCourseAndUnit")
    public ServerResponse<Map<String,Object>> getCourseAndUnit(HttpSession session) {
        return teksService.getCourseAndUnit(session);
    }

    /**
     * 查看课文最后学习时间
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTeksLaterLearnTime")
    public ServerResponse<List<Map<String, Object>>> getTeksLaterLearnTime(HttpSession session){
        return teksService.getTeksLaterLearnTime(session);
    }

    /**
     * 保存课文试听
     * @param unitId
     * @return
     */
    @ResponseBody
    @RequestMapping("saveTeksAudition")
    public ServerResponse<Map<String,Object>> saveTeksAudition(HttpSession session,Integer unitId,Integer courseId){
        return teksService.saveTeksAudition(session,unitId,courseId);
    }



    //查看课文以及翻译
    @RequestMapping("/selTeksByUnitId")
    @ResponseBody
    public Object selTeksByUnitId(Integer unitId){
        return  teksService.selTeksByUnitId(unitId);
    }

    //选择课文单词
    @RequestMapping("selChooseTeks")
    @ResponseBody
    public Object selChooseTeks(Integer unitId,HttpSession session){
        return teksService.selChooseTeks(unitId,session);
    }


    //好声音获取课文
    @RequestMapping("/selSpeakTeksByUnitId")
    @ResponseBody
    public Object selSpeakTeksByUnitId(Integer unitId,HttpSession session){
        return  teksService.selSpeakTeksByUnitId(unitId,session);
    }


    //选择课文单词
    @RequestMapping("selWriteTeks")
    @ResponseBody
    public Object selWriteTeks(Integer unitId){
        return teksService.selWriteTeks(unitId);
    }


    /**
     * 添加课文测试结果
     * @param testRecord
     * @param session
     * @return
     */
    @PostMapping("addData")
    @ResponseBody
    public ServerResponse<Object> addData(TestRecord testRecord, HttpSession session){
        return teksService.addData(testRecord,session);
    }


    /**
     * 查看历史记录
     * @param unitId
     * @param session
     * @return
     */
    @RequestMapping("selHistoryPronunciation")
    @ResponseBody
    public ServerResponse<Object> selHistoryPronunciation(Integer unitId,HttpSession session){
        return teksService.selHistoryPronunciation(unitId,session);
    }

    /**
     * 是否可以查看上次记录
     * @param unitId
     * @param session
     * @return
     */
    @RequestMapping("isHistoryPronunciation")
    @ResponseBody
    public ServerResponse<Object> isHistoryPronunciation(Integer unitId,HttpSession session){
        return teksService.isHistoryPronunciation(unitId,session);
    }


    /**
     * 查看上回记录
     * @param count
     * @param unitId
     * @param session
     * @return
     */
    @RequestMapping("selHistoryByCountAndUnitId")
    @ResponseBody
    public ServerResponse<Object> selHistoryByCountAndUnitId(Integer count,Integer unitId,HttpSession session){
        return  teksService.selHistoryByCountAndUnitId(count,unitId,session);
    }

    /**
     * 查看排行榜信息
     */
    @RequestMapping("getRanking")
    @ResponseBody
    public ServerResponse<Object> selRankingList(Integer unitId,HttpSession session){
        return teksService.selRankingList(unitId,session);
    }

    @RequestMapping("getTeksTest")
    @ResponseBody
    public ServerResponse<Object> getTeksTest(Integer unitId){
        return teksService.getTeksTest(unitId);
    }


}
