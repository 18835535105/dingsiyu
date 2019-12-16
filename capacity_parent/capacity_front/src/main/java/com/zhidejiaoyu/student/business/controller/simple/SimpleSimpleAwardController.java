package com.zhidejiaoyu.student.business.controller.simple;

import com.zhidejiaoyu.common.vo.simple.AwardVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.simple.SimpleAwardServiceSimple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 任务奖励controller
 *
 * @author wuchenxi
 * @date 2018/6/9 18:19
 */
@RestController
@RequestMapping("/api/award")
@Validated
public class SimpleSimpleAwardController {

    @Autowired
    private SimpleAwardServiceSimple simpleAwardService;

    /**
     * 获取任务奖励信息
     *
     * @param session
     * @param type    1：日奖励；2：任务奖励；3：勋章；4：膜拜
     * @return
     */
    @GetMapping("/getAwareInfo")
    public ServerResponse<List<AwardVo>> getAwardInfo(HttpSession session, @Min(0) @Max(3) @RequestParam(defaultValue = "1") Integer type) {
        return simpleAwardService.getAwareInfo(session, type);
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
        return simpleAwardService.getAware(session, awareId, type);
    }

    /**
     * 获取奖励排行数据变化个数
     *
     * @param session
     * @param type
     * @param model
     * @return
     */
    @GetMapping("/getAeardSize")
    public ServerResponse<Object> getAwareSize(HttpSession session, int type, @RequestParam(defaultValue = "0") Integer model) {
        return simpleAwardService.getAwareSize(type, session, model);
    }

    /**
     * 更新学生名次
     * 将学生名次表中的数据修改为学生当前的名次
     *
     * @param session
     * @param type  1：班级排行；2：学校排行；3：全国排行
     * @return
     */
    @PostMapping("/updateRanking")
    public ServerResponse<Object> updateRanking(HttpSession session, @RequestParam(required = false, defaultValue = "1") Integer type) {
        return simpleAwardService.updateRanking(session, type);
    }

}
