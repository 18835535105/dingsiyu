package com.zhidejiaoyu.student.controller.simple;


import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.SimpleQuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quartz")
public class SimpleQuartzController {

    @Autowired
    private SimpleQuartzService simpleQuartzService;


    @RequestMapping("/getQuartzTime")
    public String getQuarzTime(){
        simpleQuartzService.updateNews();
        return "success";
    }

    /**
     * 定时更新班级月排行信息
     *
     *
     * @return
     */
    @GetMapping("/updateClassMonthRank")
    public ServerResponse<Object> updateClassMonthRank() {
        simpleQuartzService.updateClassMonthRank();
        return ServerResponse.createBySuccess();
    }

    @GetMapping("/updateRank")
    public ServerResponse<Object> updateRank() {
        simpleQuartzService.updateRank();
        return ServerResponse.createBySuccess();
    }

    @GetMapping("/updateStudentExpansion")
    public void updateStudentExpansion(){
       simpleQuartzService.updateStudentExpansion();
    }

    @GetMapping("/deleteSessionMap")
    public void deleteSessionMap() {
        simpleQuartzService.deleteSessionMap();
    }

}
