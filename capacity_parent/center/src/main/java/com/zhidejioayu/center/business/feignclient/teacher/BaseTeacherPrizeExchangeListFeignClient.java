package com.zhidejioayu.center.business.feignclient.teacher;

import com.zhidejiaoyu.common.dto.prizeExchangeList.AddPrizeExchangeListDto;
import com.zhidejiaoyu.common.dto.prizeExchangeList.PrizeExchangeListDto;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;


public interface BaseTeacherPrizeExchangeListFeignClient {
    /**
     * 获取列表
     */
    @GetMapping(value = "/prizeExchangeList/list")
    ServerResponse<Object> list(@RequestParam PrizeExchangeListDto prizeExchangeListDto);

    /**
     * 新增
     */
    @PostMapping(value = "/prizeExchangeList/addPrizeExchangeList")
    Object addPrizeExchangeList(@RequestBody AddPrizeExchangeListDto dto);

    /**
     * 修改
     */
    @PostMapping(value = "/prizeExchangeList/updatePrizeExchangeList")
    Object updatePrizeExchangeList(@RequestBody AddPrizeExchangeListDto dto);

    /**
     * 使用id查询物品详情
     *
     * @param dto
     * @return
     */
    @GetMapping("/prizeExchangeList/selectOne")
    Object selectOne(@RequestParam AddPrizeExchangeListDto dto);

    /**
     * 批量删除
     */
    @PostMapping(value = "/prizeExchangeList/deletes")
    Object deletes(@RequestBody String openId, @RequestBody List<Integer> prizeIds);

    /**
     * 根据openId获取学校名称
     *
     * @param openId
     * @return
     */
    @GetMapping("/prizeExchangeList/getSchoolName")
    Object getSchoolName(@RequestParam String openId);


}
