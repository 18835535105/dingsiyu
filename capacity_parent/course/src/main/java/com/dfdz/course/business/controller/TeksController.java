package com.dfdz.course.business.controller;

import com.dfdz.course.business.service.TeksService;
import com.zhidejiaoyu.common.pojo.TeksNew;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/course/centerTeks")
public class TeksController {

    @Resource
    private TeksService teksService;

    /**
     * 根据unitId和group查询课文数据
     *
     * @param unitId
     * @param group
     * @return
     */
    @GetMapping("/selTeksByUnitIdAndGroup/{unitId}/{group}")
    public List<TeksNew> selTeksByUnitIdAndGroup(@PathVariable Long unitId, @PathVariable Integer group) {
        return teksService.selTeksByUnitIdAndGroup(unitId, group);
    }

    /**
     * 获取20个随机课文数据
     *
     * @return
     */
    @GetMapping("/getTwentyTeks")
    public List<TeksNew> getTwentyTeks() {
        return teksService.getTwentyTeks();
    }
}
