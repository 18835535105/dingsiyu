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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StudentExchangPrizeServiceImpl extends ServiceImpl<SimpleStudentExchangePrizeMapper, StudentExchangePrize> implements StudentExchangePrizeService {

    @Resource
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
        int index = (dto.getPageNum() - 1) * dto.getPageSize();
        for (StudentExchangePrizeVo vo : studentExchangePrizeVo) {
            index++;
            vo.setIndex(index);
            vo.setCreateTime(DateUtil.formatDate(vo.getCreateTimes(), "yyyy-MM-dd HH:mm:ss"));
        }
        PageVo<StudentExchangePrizeVo> studentManageVOPageVo = PageUtil.packagePage(studentExchangePrizeVo, page.getTotal());
        return ServerResponse.createBySuccess(studentManageVOPageVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object updateByPrizeId(Long prizeId, String openId) {
        SysUser sysUser = sysUserMapper.selectByOpenId(openId);
        Integer integer = studentExchangePrizeMapper.updateByPrizeId(prizeId, 2);
        if (integer > 0) {
            updStudent(prizeId, sysUser.getId());
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object updateByPrizeIdAndState(Long prizeId, Integer state, String openId) {
        SysUser sysUser = sysUserMapper.selectByOpenId(openId);
        Integer integer = studentExchangePrizeMapper.updateByPrizeId(prizeId, state);
        if (state != 0 && integer > 0) {
            updStudent(prizeId, sysUser.getId());
        }
        if (integer > 0) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError(300, "删除失败");
    }

    private void updStudent(Long prizeId, Integer userId) {
        StudentExchangePrize studentExchangePrize = studentExchangePrizeMapper.selectById(prizeId);
        PrizeExchangeList prizeExchangeList = prizeExchangeListMapper.selectById(studentExchangePrize.getPrizeId().intValue());

        prizeExchangeList.setSurplusNumber(prizeExchangeList.getSurplusNumber() + 1);
        prizeExchangeListMapper.updateById(prizeExchangeList);

        this.saveGoldLog(studentExchangePrize.getStudentId(), userId, -prizeExchangeList.getExchangePrize());

        Student student = studentMapper.selectById(studentExchangePrize.getStudentId());
        student.setOfflineGold(student.getOfflineGold() - prizeExchangeList.getExchangePrize());
        student.setSystemGold(student.getSystemGold() + prizeExchangeList.getExchangePrize());
        studentMapper.updateById(student);
    }

    private void saveGoldLog(Long studentId, Integer operatorId, int gold) {
        goldLogMapper.insert(GoldLog.builder()
                .studentId(studentId)
                .operatorId(operatorId)
                .reason("教师取消订购商品，金币返还")
                .readFlag(2)
                .goldReduce(gold >= 0 ? 0 : Math.abs(gold))
                .goldAdd(Math.max(gold, 0))
                .createTime(new Date())
                .type(2)
                .build());
    }
}
