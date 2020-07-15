package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.UnitService;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.apache.commons.collections.CollectionUtils;
import org.omg.CORBA.ServerRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 单元相关
 *
 * @author: wuchenxi
 * @date: 2020/7/15 16:43:43
 */
@RestController
@RequestMapping("/unit")
public class UnitController {

    @Resource
    private UnitService unitService;

    /**
     * 根据类型查询单元最大group
     *
     * @param unitIds
     * @param type    1：单词；2：句型；4：课文
     * @return
     */
    @GetMapping("/getMaxGroupByUnitIsdAndType")
    public ServerResponse<Map<Long, Integer>> getMaxGroupByUnitIsdAndType(@RequestParam List<Long> unitIds, @RequestParam Integer type) {
        if (CollectionUtils.isEmpty(unitIds)) {
            return ServerResponse.createByError(400, "unitIds can't be null!");
        }
        if (type == null) {
            return ServerResponse.createByError(400, "type can't be null!");
        }
        return unitService.getMaxGroupByUnitIsdAndType(unitIds, type);
    }

}
