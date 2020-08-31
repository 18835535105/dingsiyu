package com.zhidejioayu.center.business.prizeExchangeList.controller;

import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.dto.prizeExchangeList.AddPrizeExchangeListDto;
import com.zhidejiaoyu.common.dto.prizeExchangeList.DeletePrizeExchangeList;
import com.zhidejiaoyu.common.dto.prizeExchangeList.PrizeExchangeListDto;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.feignclient.teacher.BaseTeacherPrizeExchangeListFeignClient;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
        if (prizeExchangeListDto.getPageNum() == null || prizeExchangeListDto.getPageNum().equals(0)) {
            prizeExchangeListDto.setPageNum(1);
        }
        if (prizeExchangeListDto.getPageSize() == null || prizeExchangeListDto.getPageNum().equals(0)) {
            prizeExchangeListDto.setPageSize(20);
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getBaseTeacherPrizeExchangeListFeignClientByOpenId(prizeExchangeListDto.getOpenId());
        return prizeExchangListFeignClient.list(prizeExchangeListDto);
    }

    /**
     * 新增
     */
    @PostMapping(value = "/addPrizeExchangeList")
    public Object addPrizeExchangeList(@Valid AddPrizeExchangeListDto dto) {
        if (StringUtil.isEmpty(dto.getOpenId())) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        if(StringUtil.isEmpty(dto.getPrize())){
            return ServerResponse.createByError(400, "商品名称不能为空");
        }
        if(dto.getExchangePrize()==null){
            return ServerResponse.createByError(400, "商品价格不能为空");
        }
        if(dto.getTotalNumber()==null){
            return ServerResponse.createByError(400, "商品数量不能为空");
        }
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
        if (StringUtil.isEmpty(dto.getOpenId())) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
        if(StringUtil.isEmpty(dto.getPrize())){
            return ServerResponse.createByError(400, "商品名称不能为空");
        }
        if(dto.getExchangePrize()==null){
            return ServerResponse.createByError(400, "商品价格不能为空");
        }
        if(dto.getTotalNumber()==null){
            return ServerResponse.createByError(400, "商品数量不能为空");
        }
        if (dto.getFlag()) {
            dto.setPrizeUrl(getPrizeUrl(dto));
        }
        dto.setFile(null);
        if (dto.getPrizeUrl() == null) {
            return ServerResponse.createByError(300, "添加失败,请重新添加商品");
        }
        BaseTeacherPrizeExchangeListFeignClient prizeExchangListFeignClient = FeignClientUtil.getBaseTeacherPrizeExchangeListFeignClientByOpenId(dto.getOpenId());
        return prizeExchangListFeignClient.updatePrizeExchangeList(dto);
    }

    /**
     * 使用id查询物品详情
     *
     * @param dto
     * @return
     */
    @GetMapping("/selectOne")
    public Object selectOne(@Valid AddPrizeExchangeListDto dto) {
        if (StringUtil.isEmpty(dto.getOpenId())) {
            return ServerResponse.createByError(400, "openId can't be null!");
        }
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
            try {
                String upload = OssUpload.upload(dto.getFile(), FileConstant.PRIZE_IMG, null);
                dto.setFile(null);
                return upload;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
