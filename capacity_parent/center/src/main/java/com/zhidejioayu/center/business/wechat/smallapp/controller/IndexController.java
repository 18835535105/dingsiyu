package com.zhidejioayu.center.business.wechat.smallapp.controller;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.StudyOverviewVO;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import com.zhidejioayu.center.business.serverconfig.service.ServerConfigService;
import com.zhidejioayu.center.business.feignclient.smallapp.BaseSmallAppFeignClient;
import com.zhidejioayu.center.business.wechat.smallapp.dto.PrizeDTO;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.IndexService;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

/**
 * 首页数据接口
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:00:00
 */
@Validated
@RequestMapping("/wechat/smallApp/index")
@RestController("smallAppController")
public class IndexController {

    @Resource
    private IndexService smallAppIndexService;

    @Resource
    private ServerConfigService serverConfigService;

    /**
     * 首页数据
     *
     * @return
     */
    @GetMapping("/index")
    public ServerResponse<Object> index(String openId) {
        if (StringUtils.isEmpty(openId)) {
            throw new ServiceException("openId can't be null");
        }
        return smallAppIndexService.index(openId);
    }

    /**
     * 打卡日历信息
     *
     * @param openId
     * @return
     */
    @GetMapping("/cardInfo")
    public ServerResponse<Object> cardInfo(String openId) {
        if (StringUtils.isEmpty(openId)) {
            throw new ServiceException("openId can't be null");
        }
        return smallAppIndexService.cardInfo(openId);
    }

    /**
     * 补签
     *
     * @param date   补签的日期
     * @param openId
     * @return
     */
    @PostMapping("/replenish")
    public ServerResponse<Object> replenish(String date, String openId) {
        if (StringUtils.isEmpty(date)) {
            throw new ServiceException("date can't be null");
        }
        if (StringUtils.isEmpty(openId)) {
            throw new ServiceException("openId can't be null");
        }

        return smallAppIndexService.replenish(date, openId);
    }

    /**
     * 飞行记录（学习记录）
     *
     * @param openId
     * @return
     */
    @GetMapping("/record")
    public ServerResponse<Object> record(String openId) {
        if (StringUtils.isEmpty(openId)) {
            throw new ServiceException("openId can't be null");
        }
        return smallAppIndexService.record(openId);
    }

    /**
     * 飞行记录学习总览
     *
     * @param openId
     * @param num 扫码编号 -1：总览；其他编号：查询指定日期学习情况
     * @return
     */
    @GetMapping("/recordOverview")
    public ServerResponse<Object> recordOverview(@RequestParam String openId, @RequestParam(required = false) Integer num) {
        if (StringUtil.isEmpty(openId)) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        ServerConfig serverConfig = serverConfigService.getByStudentOpenid(openId);
        if (serverConfig == null) {
            return this.packageDefaultOverview();
        }

        BaseSmallAppFeignClient baseSmallAppFeignClient = FeignClientUtil.getBaseSmallAppFeignClient(openId);
        return baseSmallAppFeignClient.recordOverview(openId, num);
    }

    /**
     * 封装默认学习总览
     *
     * @return
     */
    private ServerResponse<Object> packageDefaultOverview() {
        return ServerResponse.createBySuccess(StudyOverviewVO.builder()
                .studyCount(0)
                .totalOnlineTime(0L)
                .totalValidTime(0L)
                .wordCount(0)
                .build());
    }

    /**
     * 飞行状态
     *
     * @param openId
     * @return
     */
    @GetMapping("/myState")
    public ServerResponse<Object> myState(String openId) {
        if (StringUtils.isEmpty(openId)) {
            throw new ServiceException("openId can't be null");
        }
        return smallAppIndexService.myState(openId);
    }

    /**
     * 藏宝阁
     *
     * @param dto
     * @return
     */
    @GetMapping("/prize")
    public ServerResponse<Object> prize(@Valid PrizeDTO dto, BindingResult result) {
        // 防止sql注入
        if (!Objects.equals(dto.getOrderBy(), "asc") && !Objects.equals(dto.getOrderBy(), "desc")) {
            dto.setOrderBy("asc");
        }
        return smallAppIndexService.prize(dto);
    }

}
