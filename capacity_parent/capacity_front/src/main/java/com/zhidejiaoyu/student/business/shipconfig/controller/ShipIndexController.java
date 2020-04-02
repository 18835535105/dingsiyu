package com.zhidejiaoyu.student.business.shipconfig.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 飞船配置首页
 *
 * @author: wuchenxi
 * @date: 2020/2/27 15:28:28
 */
@RestController
@RequestMapping("/shipIndex")
public class ShipIndexController {

    @Resource
    private ShipIndexService shipIndexService;

    /**
     * 首页数据
     *
     * @return
     */
    @GetMapping("/index")
    public ServerResponse<Object> index() {
        return shipIndexService.index();
    }

    /**
     * 源分战力排行
     *
     * @param type 1：全国排行；2：校区排行
     * @return
     */
    @GetMapping("/rank")
    public ServerResponse<Object> rank(Integer type) {
        if (type == null) {
            type = 1;
        }
        return shipIndexService.rank(type);
    }

    /**
     * 保存学生选择的勋章
     *
     * @param medalId   id之间用英文,隔开
     * @return
     */
    @PostMapping("/saveMedal")
    public ServerResponse<Object> saveMedal(String medalId) {
        if (StringUtils.isEmpty(medalId)) {
            return ServerResponse.createByError(400, "medalId can't be null!");
        }
        return shipIndexService.saveMedal(medalId);
    }
}
