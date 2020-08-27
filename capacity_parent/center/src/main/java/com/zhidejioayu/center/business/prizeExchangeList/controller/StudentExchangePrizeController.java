package com.zhidejioayu.center.business.prizeExchangeList.controller;

import com.zhidejiaoyu.common.dto.prizeExchangeList.DeletePrizeExchangeList;
import com.zhidejiaoyu.common.dto.prizeExchangeList.UpdateStudentExchangePrizeDto;
import com.zhidejiaoyu.common.dto.studentExchangePrize.StudentExchangePrizeListDto;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.feignclient.teacher.BaseTeacherPrizeExchangeListFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Validated
@RestController
@RequestMapping("/teacher/studentExchangePrize")
public class StudentExchangePrizeController {

    /**
     * 获取列表
     */
    @GetMapping(value = "/list")
    public Object list(@Valid StudentExchangePrizeListDto dto) {
        if(dto.getPageNum()==null||dto.getPageNum().equals(0)){
            dto.setPageNum(1);
        }
        if(dto.getPageSize()==null||dto.getPageNum().equals(0)){
            dto.setPageSize(20);
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getBaseTeacherPrizeExchangeListFeignClientByOpenId(dto.getOpenId());
        return prizeExchangListFeignClient.getStudentExchangePrizeList(dto);
    }

    /**
     * 删除
     */
    @PostMapping(value = "/delete")
    public Object delete(@Valid DeletePrizeExchangeList list) {
        if (StringUtil.isEmpty(list.getOpenId())) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getBaseTeacherPrizeExchangeListFeignClientByOpenId(list.getOpenId());
        return prizeExchangListFeignClient.delete(list);
    }

    /**
     * 修改
     */
    @PostMapping(value = "/updateStudent")
    public Object updateStudent(@Valid UpdateStudentExchangePrizeDto dto) {
        if (StringUtil.isEmpty(dto.getOpenId())) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getBaseTeacherPrizeExchangeListFeignClientByOpenId(dto.getOpenId());
        return prizeExchangListFeignClient.updateStudent(dto);
    }
}
