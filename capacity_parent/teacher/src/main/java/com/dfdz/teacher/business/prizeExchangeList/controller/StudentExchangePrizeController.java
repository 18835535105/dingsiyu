package com.dfdz.teacher.business.prizeExchangeList.controller;

import com.dfdz.teacher.business.prizeExchangeList.service.StudentExchangePrizeService;
import com.zhidejiaoyu.common.dto.prizeExchangeList.DeletePrizeExchangeList;
import com.zhidejiaoyu.common.dto.prizeExchangeList.UpdateStudentExchangePrizeDto;
import com.zhidejiaoyu.common.dto.studentExchangePrize.StudentExchangePrizeListDto;
import org.springframework.cloud.openfeign.SpringQueryMap;
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
    public Object list(@SpringQueryMap StudentExchangePrizeListDto dto) {
        return studentExchangePrizeService.getStudentList(dto);
    }

    /**
     * 删除
     */
    @PostMapping(value = "/delete")
    public Object delete(@RequestBody DeletePrizeExchangeList list) {
        return studentExchangePrizeService.updateByPrizeId(list.getPrizeId(), list.getOpenId());
    }

    /**
     * 修改
     */
    @PostMapping(value = "/updateStudent")
    public Object updateStudent(@RequestBody UpdateStudentExchangePrizeDto dto) {
        return studentExchangePrizeService.updateByPrizeIdAndState(dto.getPrizeId(), dto.getState(), dto.getOpenId());
    }


}
