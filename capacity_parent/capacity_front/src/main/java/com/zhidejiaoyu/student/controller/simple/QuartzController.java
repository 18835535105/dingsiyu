package com.zhidejiaoyu.student.controller.simple;


import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.QuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quartz")
public class QuartzController {

    @Autowired
    private QuartzService quartzService;


    @RequestMapping("/getQuartzTime")
    public String getQuarzTime(){
        quartzService.updateNews();
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
        quartzService.updateClassMonthRank();
        return ServerResponse.createBySuccess();
    }

    @GetMapping("/updateRank")
    public ServerResponse<Object> updateRank() {
        quartzService.updateRank();
        return ServerResponse.createBySuccess();
    }

    @GetMapping("/updateStudentExpansion")
    public void updateStudentExpansion(){
       quartzService.updateStudentExpansion();
    }

}
