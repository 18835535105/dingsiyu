package com.zhidejioayu.center.business.prizeExchangeList.controller;

import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.dto.prizeExchangeList.AddPrizeExchangeListDto;
import com.zhidejiaoyu.common.dto.prizeExchangeList.DeletePrizeExchangeList;
import com.zhidejiaoyu.common.dto.prizeExchangeList.PrizeExchangeListDto;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.feignclient.teacher.BaseTeacherPrizeExchangeListFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/teacher/prizeExchangeList")
public class PrizeExchangeListController {


    /**
     * 获取列表
     */
    @GetMapping(value = "/list")
    public ServerResponse<Object> list(@Valid PrizeExchangeListDto prizeExchangeListDto) {
        if (StringUtil.isEmpty(prizeExchangeListDto.getOpenId())) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }

        prizeExchangeListDto.setPageNum(PageUtil.getPageNum());
        prizeExchangeListDto.setPageSize(PageUtil.getPageSize());
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getBaseTeacherPrizeExchangeListFeignClientByOpenId(prizeExchangeListDto.getOpenId());
        return prizeExchangListFeignClient.list(prizeExchangeListDto);
    }

    /**
     * 新增
     */
    @PostMapping(value = "/addPrizeExchangeList")
    public Object addPrizeExchangeList(@Valid AddPrizeExchangeListDto dto) {
        dto.setPrizeUrl(getPrizeUrl(dto));
        if (dto.getPrizeUrl() == null) {
            return ServerResponse.createByError(300, "添加失败,请重新添加商品");
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getBaseTeacherPrizeExchangeListFeignClientByOpenId(dto.getOpenId());
        return prizeExchangListFeignClient.addPrizeExchangeList(dto);
    }

    /**
     * 修改
     */
    @PostMapping(value = "/updatePrizeExchangeList")
    public Object updatePrizeExchangeList(@Valid AddPrizeExchangeListDto dto) {

        if (dto.getFlag()) {
            dto.setPrizeUrl(getPrizeUrl(dto));
        }
        dto.setFile(null);
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getBaseTeacherPrizeExchangeListFeignClientByOpenId(dto.getOpenId());
        return prizeExchangListFeignClient.updatePrizeExchangeList(dto);
    }

    /**
     * 使用id查询物品详情
     *
     * @param openId
     * @param id
     * @return
     */
    @GetMapping("/selectOne")
    public Object selectOne(String openId, Long id) {
        if (StringUtil.isEmpty(openId)) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        if (id == null) {
            return ServerResponse.createByError(400, "id can't be null!");
        }
        AddPrizeExchangeListDto dto = new AddPrizeExchangeListDto();
        dto.setOpenId(openId);
        dto.setId(id);
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getBaseTeacherPrizeExchangeListFeignClientByOpenId(dto.getOpenId());
        return prizeExchangListFeignClient.selectOne(dto);
    }

    /**
     * 批量删除
     */
    @PostMapping(value = "/deletes")
    public Object deletes(@Valid DeletePrizeExchangeList list) {
        if (StringUtil.isEmpty(list.getOpenId())) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getBaseTeacherPrizeExchangeListFeignClientByOpenId(list.getOpenId());
        return prizeExchangListFeignClient.deletes(list);
    }

    /**
     * 根据openId获取学校名称
     *
     * @param openId
     * @return
     */
    @GetMapping("/getSchoolName")
    public Object getSchoolName(@RequestParam String openId) {
        if (StringUtil.isEmpty(openId)) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getBaseTeacherPrizeExchangeListFeignClientByOpenId(openId);
        return prizeExchangListFeignClient.getSchoolName(openId);
    }

    private String getPrizeUrl(AddPrizeExchangeListDto dto) {
        if (dto.getFile() != null && dto.getFile().getSize() > 0) {
            return OssUpload.upload(dto.getFile(), FileConstant.PRIZE_IMG, null);
        }
        return null;
    }
}
