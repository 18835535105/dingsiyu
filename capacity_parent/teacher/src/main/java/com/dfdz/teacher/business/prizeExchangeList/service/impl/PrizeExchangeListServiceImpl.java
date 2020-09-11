package com.dfdz.teacher.business.prizeExchangeList.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.teacher.business.prizeExchangeList.service.PrizeExchangeListService;
import com.zhidejiaoyu.aliyunoss.deleteobject.OssDelete;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.dto.prizeExchangeList.AddPrizeExchangeListDto;
import com.zhidejiaoyu.common.dto.prizeExchangeList.PrizeExchangeListDto;
import com.zhidejiaoyu.common.mapper.PrizeExchangeListMapper;
import com.zhidejiaoyu.common.mapper.SysUserMapper;
import com.zhidejiaoyu.common.mapper.TeacherMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleCampusMapper;
import com.zhidejiaoyu.common.pojo.PrizeExchangeList;
import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.pojo.Teacher;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.prizeExchangeList.PrizeExchangeListVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
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
        List<PrizeExchangeList> prizeExchangeLists = prizeExchangeListMapper.selectListBySchoolId(page, schoolAdminId, dto.getPrizeName() != null ? dto.getPrizeName().replace(" ", "") : null);

        List<PrizeExchangeListVo> vos = getPrizeExchangeListVos(prizeExchangeLists, (dto.getPageNum() - 1) * dto.getPageSize());

        Integer count = prizeExchangeListMapper.countBySchoolId(schoolAdminId);
        Page<PrizeExchangeListVo> pages = new Page<>(dto.getPageNum(), dto.getPageSize());
        pages.setTotal(count);

        PageVo<PrizeExchangeListVo> studentManageVOPageVo = PageUtil.packagePage(vos, pages.getTotal());
        return ServerResponse.createBySuccess(studentManageVOPageVo);
    }

    @Override
    public Object addPrizeExchangeList(AddPrizeExchangeListDto dto) {
        SysUser sysUser = sysUserMapper.selectByOpenId(dto.getOpenId());
        Integer schoolAdminId = teacherMapper.selectSchoolAdminIdByTeacherId(sysUser.getId().longValue());
        if (schoolAdminId == null) {
            return ServerResponse.createByError(300, "教师查询失败");
        }
        PrizeExchangeList prize = savePrizeList(dto, schoolAdminId);
        prizeExchangeListMapper.insert(prize);
        return ServerResponse.createBySuccess();
    }


    @Override
    public Object updatePrizeExchangeList(AddPrizeExchangeListDto dto) {
        SysUser sysUser = sysUserMapper.selectByOpenId(dto.getOpenId());
        Integer schoolAdminId = teacherMapper.selectSchoolAdminIdByTeacherId(sysUser.getId().longValue());
        if (schoolAdminId == null) {
            return ServerResponse.createByError(300, "教师查询失败");
        }
        PrizeExchangeList prize = savePrizeList(dto, schoolAdminId);
        prize.setId(dto.getId());
        prizeExchangeListMapper.updateById(prize);
        return ServerResponse.createBySuccess();
    }

    @Override
    public Object getSchoolName(String openId) {
        SysUser user = sysUserMapper.selectByOpenId(openId);
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
        return ServerResponse.createBySuccess(schoolName);
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
        return ServerResponse.createBySuccess(prizeExchangeList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object deletePrizes(String openId, List<Integer> prizeIds) {
        List<PrizeExchangeList> prizeExchangeLists = prizeExchangeListMapper.selectBatchIds(prizeIds);
        List<String> prizeUrls = prizeExchangeLists.stream().map(PrizeExchangeList::getPrizeUrl).collect(Collectors.toList());
        prizeExchangeListMapper.deleteBatchIds(prizeIds);

        if (CollectionUtils.isNotEmpty(prizeUrls)) {
            OssDelete.deleteObjects(prizeUrls);
        }

        return ServerResponse.createBySuccess();
    }


    private PrizeExchangeList savePrizeList(AddPrizeExchangeListDto dto, Integer schoolAdminId) {
        PrizeExchangeList prize = new PrizeExchangeList();
        prize.setSchoolId(schoolAdminId.longValue());
        prize.setCreateTime(new Date());
        prize.setDescribes(dto.getDescribes());
        prize.setPrize(dto.getPrize());
        prize.setType(dto.getType());
        prize.setExchangePrize(dto.getExchangePrize());
        prize.setPrizeUrl(dto.getPrizeUrl());
        prize.setState(1);
        prize.setSurplusNumber(dto.getTotalNumber());
        prize.setTotalNumber(dto.getTotalNumber());
        return prize;
    }

    private List<PrizeExchangeListVo> getPrizeExchangeListVos(List<PrizeExchangeList> prizeExchangeLists, Integer pages) {
        List<PrizeExchangeListVo> prizeExchangeListVos = new ArrayList<>(prizeExchangeLists.size());
        Map<Long, String> map = new HashMap<>(16);
        for (PrizeExchangeList prize : prizeExchangeLists) {
            PrizeExchangeListVo prizeExchangeListVo = new PrizeExchangeListVo();
            prizeExchangeListVo.setId(prize.getId());
            prizeExchangeListVo.setCreateTime(DateUtil.DateTime(prize.getCreateTime()));
            prizeExchangeListVo.setTotalNumber(prize.getTotalNumber());
            prizeExchangeListVo.setSurplusNumber(prize.getSurplusNumber());
            prizeExchangeListVo.setPartUrl(GetOssFile.getPublicObjectUrl(prize.getPrizeUrl()));
            prizeExchangeListVo.setExchangePrize(prize.getExchangePrize());
            prizeExchangeListVo.setPrize(prize.getPrize());
            prizeExchangeListVo.setChecked(false);
            String state = getState(prize);
            prizeExchangeListVo.setState(state);
            Long teacherId = prize.getTeacherId();
            if (teacherId != null) {
                String schoolNames = simpleCampusMapper.selSchoolName(teacherId);
                if (schoolNames != null) {
                    schoolNames = simpleCampusMapper.selSchoolName(teacherId);
                    map.put(teacherId, schoolNames);
                    prizeExchangeListVo.setSchoolName(schoolNames);
                } else {
                    Teacher teacher = teacherMapper.selectByTeacherId(teacherId);
                    if (teacher != null) {
                        prizeExchangeListVo.setSchoolName(teacher.getSchool());
                        map.put(teacherId, teacher.getSchool());
                    }
                }
            } else {
                Long schoolId = prize.getSchoolId();
                String bySchoolAdminId = teacherMapper.selectSchoolById(schoolId);
                prizeExchangeListVo.setSchoolName(bySchoolAdminId);
            }
            pages = pages + 1;
            prizeExchangeListVo.setIndex(pages);
            prizeExchangeListVos.add(prizeExchangeListVo);
        }
        return prizeExchangeListVos;
    }

    private String getState(PrizeExchangeList prize) {
        if (prize.getState() == 2) {
            return "已删除";
        }

        if (prize.getSurplusNumber() == null) {
            return "已兑空";
        }

        if (prize.getSurplusNumber() == 0) {
            return "已兑空";
        }
        return "兑换中";

    }
}
