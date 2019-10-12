package com.zhidejiaoyu.student.controller;


import com.zhidejiaoyu.common.pojo.EegRecording;
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
     * @param type    1，记忆容量  2，眼脑训练   3，火眼精金  4，最强大脑
     * @return
     */
    @RequestMapping("/getEnterMemoryCapacity")
    public ServerResponse<Object> getEnterMemoryCapacity(HttpSession session, Integer type) {
        return memoryCapacityService.getEnterMemoryCapacity(session, type);
    }

    /**
     * 判断学生是否可以进入记忆容量
     *
     * @param session
     * @return
     */
    @RequestMapping("/getReStartMemoryCapacity")
    public ServerResponse<Object> getReStartMemoryCapacity(HttpSession session,Integer type) {
        return memoryCapacityService.getReStartMemoryCapacity(session,type);
    }


    /**
     * 保存脑电波数据
     *
     * @param session
     * @param eegRecording
     * @return
     */
    @RequestMapping("/saveMemoryCapacity")
    public ServerResponse<Object> saveMemoryCapacity(HttpSession session, EegRecording eegRecording) {
        return memoryCapacityService.saveMemoryCapacity(session, eegRecording);
    }


    /**
     * 保存当日记忆容量获取金币
     *
     * @param session
     * @param grade     等级1-5
     * @param fraction  分数
     * @return
     */
    /*@RequestMapping("/saveMemoryCapacity")
    public ServerResponse<Object> saveMemoryCapacity(HttpSession session, Integer grade, Integer fraction) {
        return memoryCapacityService.saveMemoryCapacity(session, grade, fraction);
    }*/

    /**
     * 保存眼脑训练
     *
     * @param session
     * @param point   分数
     * @return
     */
    @RequestMapping("/saveTrain")
    public ServerResponse<Object> saveTrain(HttpSession session, Integer point) {
        return memoryCapacityService.saveTrain(session, point);
    }


    /**
     * 获取眼脑训练题目
     */
    @RequestMapping("/getTrainTest")
    public ServerResponse<Object> getTrainTest(HttpSession session) {
        return memoryCapacityService.getTrainTest(session);
    }

    /**
     * 火焰金晶获取单词
     *
     * @return
     */
    @RequestMapping("/getPinkeye")
    public ServerResponse<Object> getPinkeye() {
        return memoryCapacityService.getPinkeye();
    }

    /**
     * 保存火眼金睛测试记录
     *
     * @param session
     * @param point   分数
     * @return
     */
    @RequestMapping("/savePinkeye")
    public ServerResponse<Object> savePinkeye(HttpSession session, Integer point) {
        return memoryCapacityService.savePinkeye(session, point);
    }

    /**
     * 保存最强大脑数据
     *
     * @param session
     * @param point
     * @return
     */
    @RequestMapping("/saveBrain")
    public ServerResponse<Object> saveBrain(HttpSession session, Integer point) {
        return memoryCapacityService.saveBrain(session, point);
    }

}

