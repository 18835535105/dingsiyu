package com.zhidejiaoyu.student.controller;


import com.zhidejiaoyu.common.Vo.student.sentence.CourseUnitVo;
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
    @GetMapping("/getTextCourseAndUnit")
    public ServerResponse<List<CourseUnitVo>> getCourseAndUnit(HttpSession session) {
        return teksService.getCourseAndUnit(session);
    }

    //查看课文以及翻译
    @PostMapping("/selTeksByUnitId")
    @ResponseBody
    public Object selTeksByUnitId(Integer unitId){
        return  teksService.selTeksByUnitId(unitId);
    }

    //选择课文单词
    @PostMapping("selChooseTeks")
    @ResponseBody
    public Object selChooseTeks(Integer unitId){
        return teksService.selChooseTeks(unitId);
    }





















}
