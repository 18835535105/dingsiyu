package com.dfdz.teacher.business.prizeExchangeList.controller;

import com.dfdz.teacher.business.prizeExchangeList.service.StudentExchangePrizeService;
import com.zhidejiaoyu.common.dto.studentExchangePrize.StudentExchangePrizeListDto;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/studentExchangePrize")
public class StudentExchangePrizeController {


    @Resource
    private StudentExchangePrizeService studentExchangePrizeService;

    /**
     * 获取列表
     */
    @GetMapping(value = "/getStudentExchangePrizeList")
    public Object list(@RequestParam StudentExchangePrizeListDto dto) {
        return studentExchangePrizeService.getStudentList(dto);
    }

    /**
     * 删除
     */
    @PostMapping(value = "/delete")
    public Object delete(@RequestBody Long prizeId, @RequestBody String openId) {
        return studentExchangePrizeService.updateByPrizeId(prizeId, openId);
    }

    /**
     * 修改
     */
    @PostMapping(value = "/updateStudent")
    public Object updateStudent(@RequestBody Long prizeId, @RequestBody Integer state, @RequestBody String openId) {
        return studentExchangePrizeService.updateByPrizeIdAndState(prizeId, state, openId);
    }


}
