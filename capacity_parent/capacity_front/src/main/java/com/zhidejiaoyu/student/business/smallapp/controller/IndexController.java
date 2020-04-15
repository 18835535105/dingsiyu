package com.zhidejiaoyu.student.business.smallapp.controller;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import com.zhidejiaoyu.student.business.shipconfig.vo.IndexVO;
import com.zhidejiaoyu.student.business.smallapp.dto.PrizeDTO;
import com.zhidejiaoyu.student.business.smallapp.serivce.IndexService;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/smallApp/index")
@RestController("smallAppController")
public class IndexController extends BaseController {

    @Resource
    private IndexService smallAppIndexService;

    @Resource
    private ShipIndexService shipIndexService;

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
        Long studentId = smallAppIndexService.getStudentId(openId);
        IndexVO.MyState radar = shipIndexService.getBaseState(studentId);
        return ServerResponse.createBySuccess(radar);
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

    /**
     * 初始化源分战力排行
     *
     * @return
     */
    @PostMapping("/initRank")
    public ServerResponse<Object> initRank() {
        shipIndexService.initRank();
        return ServerResponse.createBySuccess();
    }
}
