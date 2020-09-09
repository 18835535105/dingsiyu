package com.dfdz.teacher.business.prizeExchangeList.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.teacher.business.prizeExchangeList.service.StudentExchangePrizeService;
import com.zhidejiaoyu.common.dto.studentExchangePrize.StudentExchangePrizeListDto;
import com.zhidejiaoyu.common.mapper.GoldLogMapper;
import com.zhidejiaoyu.common.mapper.PrizeExchangeListMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.SysUserMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleStudentExchangePrizeMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.studentExchangPrize.StudentExchangePrizeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class StudentExchangPrizeServiceImpl extends ServiceImpl<SimpleStudentExchangePrizeMapper, StudentExchangePrize> implements StudentExchangePrizeService {

    @Autowired
    private SimpleStudentExchangePrizeMapper studentExchangePrizeMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private PrizeExchangeListMapper prizeExchangeListMapper;
    @Resource
    private StudentMapper studentMapper;
    @Resource
    private GoldLogMapper goldLogMapper;

    @Override
    public Object getStudentList(StudentExchangePrizeListDto dto) {
        SysUser user = sysUserMapper.selectByOpenId(dto.getOpenId());
        List<StudentExchangePrizeVo> studentExchangePrizeVo;
        Page<StudentExchangePrizeVo> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        String studentName = StringUtil.trim(dto.getStudentName());
        if (user.getAccount().contains("xg")) {
            studentExchangePrizeVo = studentExchangePrizeMapper.selectListByAccountAndName(page, studentName, user.getId(), 1);
        } else if (user.getAccount().contains("admin")) {
            studentExchangePrizeVo = studentExchangePrizeMapper.selectListByAccountAndName(page, studentName, user.getId(), 3);
        } else {
            studentExchangePrizeVo = studentExchangePrizeMapper.selectListByAccountAndName(page, studentName, user.getId(), 2);
        }
        List<StudentExchangePrizeVo> returnList = new ArrayList<>();
        int index = (dto.getPageNum() - 1) * dto.getPageSize();
        for (StudentExchangePrizeVo vo : studentExchangePrizeVo) {
            index++;
            vo.setIndex(index);
            vo.setCreateTime(DateUtil.formatDate(vo.getCreateTimes(), "yyyy-MM-dd HH:mm:ss"));
            returnList.add(vo);
        }
        PageVo<StudentExchangePrizeVo> studentManageVOPageVo = PageUtil.packagePage(studentExchangePrizeVo, page.getTotal());
        return ServerResponse.createBySuccess(studentManageVOPageVo);
    }

    @Override
    public Object updateByPrizeId(Long prizeId, String openId) {
        SysUser sysUser = sysUserMapper.selectByOpenId(openId);
        Integer integer = studentExchangePrizeMapper.updateByPrizeId(prizeId, 2);
        updStudent(prizeId, integer, sysUser.getId());
        return ServerResponse.createBySuccess();
    }

    @Override
    public Object updateByPrizeIdAndState(Long prizeId, Integer state, String openId) {
        SysUser sysUser = sysUserMapper.selectByOpenId(openId);
        Integer integer = studentExchangePrizeMapper.updateByPrizeId(prizeId, state);
        if (state != 0) {
            updStudent(prizeId, integer, sysUser.getId());
        }
        if (integer > 0) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError(300, "删除失败");
    }

    private void updStudent(Long prizeId, Integer integer, Integer userId) {
        StudentExchangePrize studentExchangePrize = studentExchangePrizeMapper.selectById(prizeId);
        PrizeExchangeList prizeExchangeList = prizeExchangeListMapper.selectById(studentExchangePrize.getPrizeId().intValue());
        if (integer > 0) {
            this.saveGoldLog(studentExchangePrize.getStudentId(), userId, "教师取消订购商品，金币返还", -prizeExchangeList.getExchangePrize(), 2);
            Student student = studentMapper.selectById(studentExchangePrize.getStudentId());
            student.setOfflineGold(student.getOfflineGold() - prizeExchangeList.getExchangePrize());
            student.setSystemGold(student.getSystemGold() + prizeExchangeList.getExchangePrize());
            studentMapper.updateById(student);
        }
    }

    private void saveGoldLog(Long studentId, Integer operatorId, String reason, int gold, int type) {
        goldLogMapper.insert(GoldLog.builder()
                .studentId(studentId)
                .operatorId(operatorId)
                .reason(reason)
                .readFlag(2)
                .goldReduce(gold >= 0 ? 0 : Math.abs(gold))
                .goldAdd(Math.max(gold, 0))
                .createTime(new Date())
                .type(type)
                .build());
    }
}
