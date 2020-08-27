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
    public ServerResponse<Object> list(@RequestParam PrizeExchangeListDto prizeExchangeListDto) {
        return prizeExchangeListService.getAllList(prizeExchangeListDto);
    }


    /**
     * 新增
     */
    @PostMapping(value = "/addPrizeExchangeList")
    @ResponseBody
    public Object addPrizeExchangeList(@RequestBody AddPrizeExchangeListDto dto) {
        return prizeExchangeListService.addPrizeExchangeList(dto);
    }

    /**
     * 修改
     */
    @PostMapping(value = "/updatePrizeExchangeList")
    @ResponseBody
    public Object updatePrizeExchangeList(@RequestBody AddPrizeExchangeListDto dto) {
        return prizeExchangeListService.updatePrizeExchangeList(dto);
    }

    /**
     * 使用id查询物品详情
     * @param dto
     * @return
     */
    @GetMapping("/selectOne")
    @ResponseBody
    public Object selectOne(@RequestBody AddPrizeExchangeListDto dto){
        return prizeExchangeListService.selectByPirzeId(dto);
    }

    /**
     * 根据openId获取学校名称
     * @param openId
     * @return
     */
    @GetMapping("/getSchoolName")
    @ResponseBody
    public Object getSchoolName(@RequestParam String openId){
        return prizeExchangeListService.getSchoolName(openId);
    }

    /**
     * 批量删除
     */
    @PostMapping(value = "/deletes")
    @ResponseBody
    public Object deletes(@RequestBody String openId,@RequestBody List<Integer> prizeIds) {
        return prizeExchangeListService.deletePrizes(openId,prizeIds);
    }



}
