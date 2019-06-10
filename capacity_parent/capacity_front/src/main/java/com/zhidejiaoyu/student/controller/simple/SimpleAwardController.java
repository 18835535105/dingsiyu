package com.zhidejiaoyu.student.controller.simple;

import com.zhidejiaoyu.common.Vo.simple.AwardVo;
import com.zhidejiaoyu.common.utils.simple.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.AwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 任务奖励controller
 *
 * @author wuchenxi
 * @date 2018/6/9 18:19
 */
@RestController
@RequestMapping("/api/award")
@Validated
public class SimpleAwardController {

    @Autowired
    private AwardService awardService;

    /**
     * 获取任务奖励信息
     *
     * @param session
     * @param type    1：日奖励；2：任务奖励；3：勋章；4：膜拜
     * @return
     */
    @GetMapping("/getAwareInfo")
    public ServerResponse<List<AwardVo>> getAwardInfo(HttpSession session, @Min(0) @Max(3) @RequestParam(defaultValue = "1") Integer type) {
        return awardService.getAwareInfo(session, type);
    }

    /**
     * 领取奖励
     *
     * @param session
     * @param awareId 奖励id
     * @param type    领取类型：1：领取金币将励；2：领取勋章奖励
     * @return
     */
    @PostMapping("/getAware")
    public ServerResponse<String> getAware(HttpSession session, @NotNull Long awareId, @Min(value = 0) @Max(value = 2) Integer type) {
        return awardService.getAware(session, awareId, type);
    }

    @GetMapping("/getAeardSize")
    public ServerResponse<Map<String, Object>> getAwareSize(HttpSession session, int type, @RequestParam(defaultValue = "0") Integer model) {
        return awardService.getAwareSize(type, session, model);
    }


}
