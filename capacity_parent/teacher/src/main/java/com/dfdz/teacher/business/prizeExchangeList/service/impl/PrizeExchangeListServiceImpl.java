package com.dfdz.teacher.business.prizeExchangeList.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.teacher.business.prizeExchangeList.service.PrizeExchangeListService;
import com.zhidejiaoyu.aliyunoss.deleteobject.OssDelete;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.dto.prizeExchangeList.AddPrizeExchangeListDto;
import com.zhidejiaoyu.common.dto.prizeExchangeList.PrizeExchangeListDto;
import com.zhidejiaoyu.common.mapper.PrizeExchangeListMapper;
import com.zhidejiaoyu.common.mapper.SysUserMapper;
import com.zhidejiaoyu.common.mapper.TeacherMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleCampusMapper;
import com.zhidejiaoyu.common.pojo.PrizeExchangeList;
import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrizeExchangeListServiceImpl extends ServiceImpl<PrizeExchangeListMapper, PrizeExchangeList> implements PrizeExchangeListService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private PrizeExchangeListMapper prizeExchangeListMapper;
    @Resource
    private SimpleCampusMapper simpleCampusMapper;

    @Override
    public ServerResponse<Object> getAllList(PrizeExchangeListDto dto) {
        SysUser sysUser = sysUserMapper.selectByOpenId(dto.getOpenId());
        Integer schoolAdminId = teacherMapper.selectSchoolAdminIdByTeacherId(sysUser.getId().longValue());
        Page<PrizeExchangeList> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        List<PrizeExchangeList> prizeExchangeLists = prizeExchangeListMapper.selectBySchoolId(schoolAdminId, "3", null);
        PageVo<PrizeExchangeList> studentManageVOPageVo = PageUtil.packagePage(prizeExchangeLists, page.getTotal());
        return ServerResponse.createBySuccess(studentManageVOPageVo);
    }

    @Override
    public Object addPrizeExchangeList(AddPrizeExchangeListDto dto) {
        SysUser sysUser = sysUserMapper.selectByOpenId(dto.getOpenId());
        Integer schoolAdminId = teacherMapper.selectSchoolAdminIdByTeacherId(sysUser.getId().longValue());
        String fileName = "";
        if (schoolAdminId == null) {
            return ServerResponse.createByError(300, "教师查询失败");
        }
        fileName = getPrizeUrl(dto);
        if (fileName == null) {
            return ServerResponse.createByError(300, "添加失败,请重新添加商品");
        }
        PrizeExchangeList prize = savePrizeList(dto, schoolAdminId, fileName);
        prizeExchangeListMapper.insert(prize);
        return ServerResponse.createBySuccess();
    }


    @Override
    public Object updatePrizeExchangeList(AddPrizeExchangeListDto dto) {
        SysUser sysUser = sysUserMapper.selectByOpenId(dto.getOpenId());
        Integer schoolAdminId = teacherMapper.selectSchoolAdminIdByTeacherId(sysUser.getId().longValue());
        String fileName = "";
        if (schoolAdminId == null) {
            return ServerResponse.createByError(300, "教师查询失败");
        }
        if(dto.getFalg()){
            fileName = getPrizeUrl(dto);
        }else{
            fileName=dto.getPrizeUrl();
        }

        if (fileName == null) {
            return ServerResponse.createByError(300, "添加失败,请重新添加商品");
        }
        PrizeExchangeList prize = savePrizeList(dto, schoolAdminId, fileName);
        prize.setId(dto.getId());
        prizeExchangeListMapper.updateById(prize);
        return ServerResponse.createBySuccess();
    }

    @Override
    public Object getSchoolName(String openId) {
       SysUser user=sysUserMapper.selectByOpenId(openId);
        List<String> schoolName = new ArrayList<>();
        if (user.getAccount().contains("xg")) {
            List<Long> longs = teacherMapper.selectTeacherIdsBySchoolAdminId(user.getId());
            schoolName.add("直属学校");
            if (longs != null && longs.size() > 0) {
                List<String> schoolNames = simpleCampusMapper.getSchoolName(longs);
                if (schoolNames != null && schoolNames.size() > 0) {
                    schoolName.addAll(schoolNames);
                }
            }
        } else {
            String s = simpleCampusMapper.selSchoolName(user.getId().longValue());
            schoolName.add(s);
        }
        return schoolName;
    }

    @Override
    public Object selectByPirzeId(AddPrizeExchangeListDto dto) {
        PrizeExchangeList prizeExchangeList = prizeExchangeListMapper.selectById(dto.getId());
        if (prizeExchangeList.getTeacherId() != null) {
            String schoolName = simpleCampusMapper.selSchoolName(prizeExchangeList.getTeacherId());
            prizeExchangeList.setSchoolName(schoolName);
        } else {
            prizeExchangeList.setSchoolName("直属学校");
        }
        prizeExchangeList.setPrizeUrl(GetOssFile.getPublicObjectUrl(prizeExchangeList.getPrizeUrl()));
        return prizeExchangeList;
    }

    @Override
    public Object deletePrizes(String openId, List<Integer> prizeIds) {
        List<PrizeExchangeList> prizeExchangeLists = prizeExchangeListMapper.selectBatchIds(prizeIds);
        Integer integer = prizeExchangeListMapper.deleteBatchIds(prizeIds);
        if (prizeExchangeLists.size() > 0) {
            OssDelete.deleteObjects(prizeExchangeLists.stream().map(PrizeExchangeList::getPrizeUrl).collect(Collectors.toList()));
        }
        return ServerResponse.createBySuccess();
    }


    private String getPrizeUrl(AddPrizeExchangeListDto dto) {
        if (dto.getFile() != null && dto.getFile().getSize() > 0) {
            try {
                return OssUpload.upload(dto.getFile(), FileConstant.PRIZE_IMG, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private PrizeExchangeList savePrizeList(AddPrizeExchangeListDto dto, Integer schoolAdminId, String fileName) {
        PrizeExchangeList prize = new PrizeExchangeList();
        prize.setSchoolId(schoolAdminId.longValue());
        prize.setCreateTime(new Date());
        prize.setDescribes(dto.getDescribes());
        prize.setPrize(dto.getPrize());
        prize.setExchangePrize(dto.getExchangePrize());
        prize.setPrizeUrl(fileName);
        prize.setState(1);
        prize.setSurplusNumber(dto.getTotalNumber());
        prize.setTotalNumber(dto.getTotalNumber());
        return prize;
    }
}
