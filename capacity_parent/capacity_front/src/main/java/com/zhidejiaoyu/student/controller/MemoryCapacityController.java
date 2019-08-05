package com.zhidejiaoyu.student.controller;


import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.MemoryCapacityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zdjy
 * @since 2019-07-29
 */
@RestController
@RequestMapping("/memoryCapacity")
public class MemoryCapacityController {

    @Autowired
    private MemoryCapacityService memoryCapacityService;

    /**
     * 判断学生是否可以进入记忆容量
     *
     * @param session
     */
    @RequestMapping("/getEnterMemoryCapacity")
    public ServerResponse<Object> getEnterMemoryCapacity(HttpSession session, Integer type) {
        return memoryCapacityService.getEnterMemoryCapacity(session, type);
    }

    /**
     * 保存当日记忆容量获取金币
     *
     * @param session
     * @param grade
     * @param fraction
     * @return
     */
    @RequestMapping("/saveMemoryCapacity")
    public ServerResponse<Object> saveMemoryCapacity(HttpSession session, Integer grade, Integer fraction) {
        return memoryCapacityService.saveMemoryCapacity(session, grade, fraction);
    }

    /**
     * 保存眼脑训练
     *
     * @param session
     * @param point
     * @return
     */
    @RequestMapping("/saveTrain")
    public ServerResponse<Object> saveTrain(HttpSession session, Integer point) {
        return memoryCapacityService.saveTrain(session, point);
    }

    /**
     *获取眼脑训练题目
     */
    @RequestMapping("/getTrainTest")
    public ServerResponse<Object> getTrainTest(HttpSession session){
        return memoryCapacityService.getTrainTest(session);
    }

    /**
     * 火焰金晶获取单词
     * @return
     */
    @RequestMapping("/getPinkeye")
    public ServerResponse<Object> getPinkeye(){
        return memoryCapacityService.getPinkeye();
    }

    @RequestMapping("/savePinkeye")
    public ServerResponse<Object> savePinkeye(HttpSession session,Integer point){
        return memoryCapacityService.savePinkeye(session,point);
    }

}

