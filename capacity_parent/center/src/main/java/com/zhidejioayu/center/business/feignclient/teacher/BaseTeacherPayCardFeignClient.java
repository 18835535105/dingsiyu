package com.zhidejioayu.center.business.feignclient.teacher;

import com.zhidejiaoyu.common.dto.wechat.qy.teacher.PayStudentsDTO;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface BaseTeacherPayCardFeignClient {

    /**
     * 单人课时添加
     *
     * @param studentUUID
     * @param type
     * @param openId
     * @return
     */
    @PostMapping("/payCard/pay")
    Object pay(@RequestParam String studentUUID, @RequestParam Integer type, @RequestParam String openId);

    /**
     * 多人课时添加
     *
     * @param dto
     * @return
     */
    @PostMapping("/payCard/addMoreStudent")
    Object addAllStudent(@RequestBody PayStudentsDTO dto);

    /**
     * 获取学管队长币
     *
     * @param openId    学管openId
     * @return
     */
    @GetMapping("/payCard/getCapacityCoin")
    ServerResponse<Object> getCapacityCoin(@RequestParam String openId);
}
