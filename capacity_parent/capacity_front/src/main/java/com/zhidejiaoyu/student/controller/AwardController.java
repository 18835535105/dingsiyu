package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.AwardService;
import com.zhidejiaoyu.student.vo.AwardVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
@Slf4j
@RestController
@RequestMapping("/award")
@Validated
public class AwardController extends BaseController {

    @Value("${domain}")
    private String domain;

    @Autowired
    private AwardService awardService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取任务奖励信息
     *
     * @param session
     * @param type    1：日奖励；2：任务奖励；3：勋章
     * @return
     */
    @GetMapping("/getAwareInfo")
    public ServerResponse<List<AwardVo>> getAwardInfo(HttpSession session, @Min(0)@Max(3)@RequestParam(defaultValue = "1") Integer type) {
        return awardService.getAwareInfo(session, type);
    }

    /**
     * 领取奖励
     *
     * @param session
     * @param awareId 奖励id
     * @param type 领取类型：1：领取金币将励；2：领取勋章奖励
     * @return
     */
    @PostMapping("/getAware")
    public ServerResponse<String> getAware(HttpSession session, @NotNull Long awareId, @Min(value = 0)@Max(value = 2) Integer type) {
        return awardService.getAware(session, awareId, type);
    }

    @GetMapping("/getAeardSize")
    public ServerResponse<Object> getAwardSize(HttpSession session, int type, @RequestParam(defaultValue = "0") Integer model) {
        Map<String, Object> paramMap = super.packageParams(session);
        paramMap.put("type", type);
        paramMap.put("model", model);

        String url = domain + "/api/award/getAeardSize?type={type}&model={model}&session={session}&studentId={studentId}&loginTime={loginTime}";
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class, paramMap);
        return ServerResponse.createBySuccess(responseEntity.getBody() == null ? null : responseEntity.getBody().get("data"));
    }
}
