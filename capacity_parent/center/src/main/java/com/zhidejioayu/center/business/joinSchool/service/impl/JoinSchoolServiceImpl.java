package com.zhidejioayu.center.business.joinSchool.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.JoinSchoolMapper;
import com.zhidejiaoyu.common.pojo.JoinSchool;
import com.zhidejiaoyu.common.utils.IdUtil;
import com.zhidejiaoyu.common.vo.WebSocketMessage;
import com.zhidejioayu.center.business.joinSchool.service.JoinSchoolService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Service
public class JoinSchoolServiceImpl extends ServiceImpl<JoinSchoolMapper, JoinSchool> implements JoinSchoolService {

    @Resource
    private JoinSchoolMapper joinSchoolMapper;

    /**
     * 根据地址查询学校
     *
     * @param address 地址 支持模糊查询
     * @return
     */
    @Override
    public Map<String, Object> selSchoolByAddress(String address) {
        //如果地址为空,给地址一个值
        if (address == null || address.equals("null")) {
            address = "";
        }
        //获取地址,去除可能打上的空格
        address = address.trim();
        //根据查询内容查询学校
        List<JoinSchool> joinSchools = joinSchoolMapper.selectByLikeAddress(address);
        Map<String, Object> map = new HashMap<>();
        map.put("status", 200);
        map.put("data", joinSchools);
        return map;
    }

    /**
     * 加盟校添加方法
     *
     * @param joinSchool 加盟校对象信息
     * @return
     */
    @Override
    public Map<String, Object> addService(JoinSchool joinSchool) {
        //创建返回map
        Map<String, Object> map = new HashMap<>();
        //判断学校名,校长名,手机号不为空
        if (joinSchool.getSchoolName() == null || joinSchool.getPessidentName() == null || joinSchool.getPhone() == null
                || "".equals(joinSchool.getSchoolName().trim()) || "".equals(joinSchool.getPessidentName().trim()) || "".equals(joinSchool.getPhone().trim())) {
            map.put("status", 400);
            map.put("msg", "校长名字,手机号,学校名称不能为空");
            return map;
        }
        //查询加盟校预约单号最大值
        Integer reservationNumber = joinSchoolMapper.selectMaxReservation();

        //预约单号赋值
        if (reservationNumber != null && reservationNumber != 0) {
            reservationNumber += 1;
        } else {
            reservationNumber = (Math.round(10)) * 10000;
        }
        //判断市是否为空,如果为空,添加市信息
        if (joinSchool.getCity() == null || "".equals(joinSchool.getCity().trim())) {
            joinSchool.setCity(joinSchool.getProvince());
        }
        //添加对象信息
        joinSchool.setReservationNumber(reservationNumber);
        joinSchool.setDateOfJoining(new Date());
        joinSchool.setAuditStatus(2);
        joinSchool.setReporting(4);
        String address = joinSchool.getAddress();
        address = address.replace("& #40;", "(");
        address = address.replace("& #41;", ")");
        joinSchool.setAddress(address);
        joinSchool.setId(IdUtil.getId());
        Integer insert = joinSchoolMapper.insert(joinSchool);
        map.put("insert", insert);
        map.put("id", joinSchool.getId());
        return map;
    }

}
