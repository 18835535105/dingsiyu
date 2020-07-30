package com.zhidejioayu.center.business.feignclient.teacher;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BaseTeacherPayCardFeignClient {

    /**
     * 单人课时添加
     *
     * @param studentUUID
     * @param type
     * @param adminUUID
     * @return
     */
    @PostMapping("/teacher/payCard/pay")
    Object pay(@RequestParam String studentUUID, @RequestParam Integer type, @RequestParam String adminUUID);

    /**
     * 多人课时添加
     *
     * @param studentIds
     * @param type
     * @param adminUUID
     * @return
     */
    @PostMapping("/teacher/payCard/addMoreStudent")
    Object addAllStudent(@RequestBody List<String> studentIds, @RequestParam("type") Integer type, @RequestParam("adminUUID") String adminUUID);
}
