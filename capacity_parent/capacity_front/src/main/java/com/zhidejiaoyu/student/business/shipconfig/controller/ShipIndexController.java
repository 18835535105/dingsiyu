package com.zhidejiaoyu.student.business.shipconfig.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.IndexVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    public ServerResponse<IndexVO> index() {
        return shipIndexService.index();
    }

    /**
     * 查看其他人的飞船配置页面
     *
     * @param studentId 指定学生id
     * @return
     */
    @GetMapping("/other/index")
    public ServerResponse<Object> otherIndex(Long studentId) {
        if (studentId == null) {
            return ServerResponse.createByError(400, "studentId can't be null!");
        }
        return shipIndexService.otherIndex(studentId);
    }

    /**
     * 源分战力排行
     *
     * @param type 1：全国排行；2：校区排行；3：同服务器排行
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
     * @param medalId id之间用英文,隔开
     * @return
     */
    @PostMapping("/saveMedal")
    public ServerResponse<Object> saveMedal(String medalId) {
        if (StringUtils.isEmpty(medalId)) {
            return ServerResponse.createByError(400, "medalId can't be null!");
        }
        return shipIndexService.saveMedal(medalId);
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
