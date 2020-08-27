package com.dfdz.teacher.business.prizeExchangeList.controller;

import com.dfdz.teacher.business.prizeExchangeList.service.PrizeExchangeListService;
import com.zhidejiaoyu.common.dto.prizeExchangeList.AddPrizeExchangeListDto;
import com.zhidejiaoyu.common.dto.prizeExchangeList.PrizeExchangeListDto;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/prizeExchangeList")
public class PrizeExchangeListController {

    @Resource
    private PrizeExchangeListService prizeExchangeListService;


    /**
     * 获取列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public ServerResponse<Object> list(@RequestBody PrizeExchangeListDto prizeExchangeListDto) {
        return prizeExchangeListService.getAllList(prizeExchangeListDto);
    }


    /**
     * 新增
     */
    @RequestMapping(value = "/addPrizeExchangeList")
    @ResponseBody
    public Object addPrizeExchangeList(@RequestBody AddPrizeExchangeListDto dto) {
        return prizeExchangeListService.addPrizeExchangeList(dto);
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/updatePrizeExchangeList")
    @ResponseBody
    public Object updatePrizeExchangeList(@RequestBody AddPrizeExchangeListDto dto) {
        return prizeExchangeListService.updatePrizeExchangeList(dto);
    }

    /**
     * 使用id查询物品详情
     * @param dto
     * @return
     */
    @RequestMapping("/selectOne")
    @ResponseBody
    public Object selectOne(@RequestBody AddPrizeExchangeListDto dto){
        return prizeExchangeListService.selectByPirzeId(dto);
    }


    @RequestMapping("/getSchoolName")
    @ResponseBody
    public Object getSchoolName(String openId){
        return prizeExchangeListService.getSchoolName(openId);
    }

    /**
     * 批量删除
     */
    @RequestMapping(value = "/deletes")
    @ResponseBody
    public Object deletes(@RequestBody String openId,@RequestBody List<Integer> prizeIds) {
        return prizeExchangeListService.deletePrizes(openId,prizeIds);
    }



}
