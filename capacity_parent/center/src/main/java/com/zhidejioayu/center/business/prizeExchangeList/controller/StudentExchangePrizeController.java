package com.zhidejioayu.center.business.prizeExchangeList.controller;

import com.zhidejiaoyu.common.dto.studentExchangePrize.StudentExchangePrizeListDto;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.feignclient.teacher.BaseTeacherPrizeExchangeListFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RestController
@RequestMapping("/teacher/studentExchangePrize")
public class StudentExchangePrizeController {

    /**
     * 获取列表
     */
    @GetMapping(value = "/list")
    public Object list(@RequestParam StudentExchangePrizeListDto dto) {
        if(dto.getPageNum()==null||dto.getPageNum().equals(0)){
            dto.setPageNum(1);
        }
        if(dto.getPageSize()==null||dto.getPageNum().equals(0)){
            dto.setPageSize(20);
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getPrizeExchangListFeignClient(dto.getOpenId());
        return prizeExchangListFeignClient.getStudentExchangePrizeList(dto);
    }

    /**
     * 删除
     */
    @PostMapping(value = "/delete")
    public Object delete(@RequestBody Long prizeId, @RequestBody String openId) {
        if (StringUtil.isEmpty(openId)) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getPrizeExchangListFeignClient(openId);
        return prizeExchangListFeignClient.delete(prizeId, openId);
    }

    /**
     * 修改
     */
    @PostMapping(value = "/updateStudent")
    public Object updateStudent(@RequestBody Long prizeId, @RequestBody Integer state, @RequestBody String openId) {
        if (StringUtil.isEmpty(openId)) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getPrizeExchangListFeignClient(openId);
        return prizeExchangListFeignClient.updateStudent(prizeId, state, openId);
    }
}
