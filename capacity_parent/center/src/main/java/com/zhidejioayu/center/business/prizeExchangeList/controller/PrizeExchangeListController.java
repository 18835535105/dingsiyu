package com.zhidejioayu.center.business.prizeExchangeList.controller;

import com.zhidejiaoyu.common.dto.prizeExchangeList.AddPrizeExchangeListDto;
import com.zhidejiaoyu.common.dto.prizeExchangeList.PrizeExchangeListDto;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.feignclient.teacher.BaseTeacherPrizeExchangeListFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/teacher/prizeExchangeList")
public class PrizeExchangeListController {


    /**
     * 获取列表
     */
    @GetMapping(value = "/list")
    public ServerResponse<Object> list(@RequestParam PrizeExchangeListDto prizeExchangeListDto) {
        if (StringUtil.isEmpty(prizeExchangeListDto.getOpenId())) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        if(prizeExchangeListDto.getPageNum()==null||prizeExchangeListDto.getPageNum().equals(0)){
            prizeExchangeListDto.setPageNum(1);
        }
        if(prizeExchangeListDto.getPageSize()==null||prizeExchangeListDto.getPageNum().equals(0)){
            prizeExchangeListDto.setPageSize(20);
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getPrizeExchangListFeignClient(prizeExchangeListDto.getOpenId());
        return prizeExchangListFeignClient.list(prizeExchangeListDto);
    }

    /**
     * 新增
     */
    @PostMapping(value = "/addPrizeExchangeList")
    public Object addPrizeExchangeList(@RequestBody AddPrizeExchangeListDto dto){
        if (StringUtil.isEmpty(dto.getOpenId())) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getPrizeExchangListFeignClient(dto.getOpenId());
        return prizeExchangListFeignClient.addPrizeExchangeList(dto);
    }

    /**
     * 修改
     */
    @PostMapping(value = "/updatePrizeExchangeList")
    public Object updatePrizeExchangeList(@RequestBody AddPrizeExchangeListDto dto){
        if (StringUtil.isEmpty(dto.getOpenId())) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getPrizeExchangListFeignClient(dto.getOpenId());
        return prizeExchangListFeignClient.updatePrizeExchangeList(dto);
    }

    /**
     * 使用id查询物品详情
     *
     * @param dto
     * @return
     */
    @GetMapping("/selectOne")
    public Object selectOne(@RequestParam AddPrizeExchangeListDto dto){
        if (StringUtil.isEmpty(dto.getOpenId())) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getPrizeExchangListFeignClient(dto.getOpenId());
        return prizeExchangListFeignClient.selectOne(dto);
    }

    /**
     * 批量删除
     */
    @PostMapping(value = "/deletes")
    public Object deletes(@RequestBody String openId, @RequestBody List<Integer> prizeIds){
        if (StringUtil.isEmpty(openId)) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getPrizeExchangListFeignClient(openId);
        return prizeExchangListFeignClient.deletes(openId,prizeIds);
    }

    /**
     * 根据openId获取学校名称
     *
     * @param openId
     * @return
     */
    @GetMapping("/getSchoolName")
    public Object getSchoolName(@RequestParam String openId){
        if (StringUtil.isEmpty(openId)) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getPrizeExchangListFeignClient(openId);
        return prizeExchangListFeignClient.getSchoolName(openId);
    }
}
