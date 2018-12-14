package com.zhidejiaoyu.student.controller;


import com.zhidejiaoyu.student.service.TeksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/teks")
public class TeksController  {


    @Autowired
    private TeksService teksService;

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
