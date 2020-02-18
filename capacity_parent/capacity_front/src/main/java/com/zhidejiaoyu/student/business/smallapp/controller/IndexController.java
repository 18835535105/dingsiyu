package com.zhidejiaoyu.student.business.smallapp.controller;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.smallapp.serivce.IndexService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 首页数据接口
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:00:00
 */
@RequestMapping("/smallApp/index")
@RestController("smallAppController")
public class IndexController extends BaseController {

    @Resource
    private IndexService smallAppIndexService;

    /**
     * 首页数据
     *
     * @return
     */
    @GetMapping("/index")
    public ServerResponse<Object> index() {
        return smallAppIndexService.index();
    }

    /**
     * 补签
     *
     * @param date 补签的日期
     * @return
     */
    @PostMapping("/replenish")
    public ServerResponse<Object> replenish(String date) {
        if (StringUtils.isEmpty(date)) {
            throw new ServiceException("date can't be null");
        }

        return smallAppIndexService.replenish(date);
    }

    /**
     * 飞行记录（学习记录）
     *
     * @return
     */
    @GetMapping("/record")
    public ServerResponse<Object> record() {
        return smallAppIndexService.record();
    }

    /**
     * 飞行状态
     *
     * @return
     */
    @GetMapping("/myState")
    public ServerResponse<Object> myState() {
        return smallAppIndexService.myState();
    }
}
