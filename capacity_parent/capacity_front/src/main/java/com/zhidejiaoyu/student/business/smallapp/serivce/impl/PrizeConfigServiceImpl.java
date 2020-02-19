package com.zhidejiaoyu.student.business.smallapp.serivce.impl;

import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.constant.test.StudyModelConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.smallapp.serivce.PrizeConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class PrizeConfigServiceImpl extends BaseServiceImpl<PrizeConfigMapper, PrizeConfig> implements PrizeConfigService {

    @Resource
    private StudentPayConfigMapper studentPayConfigMapper;
    @Resource
    private PrizeConfigMapper prizeConfigMapper;
    @Resource
    private StudentMapper studentMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private TestRecordMapper testRecordMapper;
    @Resource
    private ShareConfigMapper shareConfigMapper;

    @Override
    public Object getPrizeConfig(String openId, Long adminId, Long studentId, String weChatimgUrl, String weChatName) {
        //判断当前openId是否已经领取过今日的奖品
        Date date = new Date();
        Map<String, Object> returnMap = new HashMap<>();
        Long payconfigId;
        StudentPayConfig studentPayConfig = studentPayConfigMapper.selectByWenXiIdAndDate(openId, date);
        if (studentPayConfig == null) {
            //获取学校奖品数据
            List<PrizeConfig> prizeConfigs = prizeConfigMapper.selectByAdminId(adminId);
            payconfigId = this.getPrize(prizeConfigs).longValue();
            //获取图片
            studentPayConfig = new StudentPayConfig();
            studentPayConfig.setCreateTime(date);
            studentPayConfig.setPrizeConfigId(payconfigId);
            studentPayConfig.setWenXinId(openId);
            studentPayConfig.setObtain("" + date.getTime() + new Random(1000).nextInt());
            studentPayConfig.setWeChatImgUrl(weChatimgUrl);
            studentPayConfig.setWeChatName(weChatName);
            studentPayConfig.setStudentId(studentId);
            studentPayConfigMapper.insert(studentPayConfig);
            Student student = studentMapper.selectById(studentId);
            student.setSystemGold(student.getSystemGold() + 5);
            studentMapper.updateById(student);
        } else {
            payconfigId = studentPayConfig.getPrizeConfigId();
        }
        PrizeConfig prizeConfig = prizeConfigMapper.selectById(payconfigId);
        returnMap.put("prizeName", prizeConfig.getPrizeName());
        SysUser sysUser = sysUserMapper.selectById(adminId);
        returnMap.put("adminPhone", sysUser.getPhone());
        ShareConfig shareConfig = shareConfigMapper.selectByAdminId(adminId.intValue());
        if (shareConfig != null) {
            returnMap.put("background", shareConfig.getImgUrl());
        } else {
            returnMap.put("background", null);
        }
        returnMap.put("obtain", studentPayConfig.getObtain());
        return returnMap;
    }

    @Override
    public Object getAdmin(Long studentId) {
        Student student = studentMapper.selectById(studentId);
        Integer adminId = teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId());
        TestRecord testRecord = testRecordMapper.selectByStudentIdAndGenreAndStudyModel(student.getId(), GenreConstant.SMALLAPP_GENRE, StudyModelConstant.SMALLAPP_STUDY_MODEL);
        String imgUrl = shareConfigMapper.selectImgByAdminId(adminId);
        Map<String, Object> map = new HashMap<>();
        map.put("adminId", adminId);
        map.put("weChatList", studentPayConfigMapper.selectWeChatNameAndWeChatImgUrlByStudentId(studentId));
        map.put("point", testRecord.getPoint());
        map.put("imgUrl", imgUrl);
        map.put("studentId", student.getId());
        return map;
    }

    private Integer getPrize(List<PrizeConfig> prizeConfigs) {
        List<Map<String, Integer>> prizeList = new ArrayList<>();
        Map<String, Integer> integer = new HashMap<>();
        prizeConfigs.forEach(prize -> {
            Double dou = (prize.getChance() * 100);
            int addInt = dou.intValue();
            Integer anInt = integer.get("int");
            Integer minInt;
            Integer maxInt;
            if (anInt == null) {
                minInt = 0;
                maxInt = addInt;
                integer.put("int", maxInt);
            } else {
                minInt = anInt;
                maxInt = anInt + addInt;
                integer.put("int", maxInt);
            }
            Map<String, Integer> transferMap = new HashMap<>();
            transferMap.put("minInt", minInt);
            transferMap.put("maxInt", maxInt);
            transferMap.put("prizeId", prize.getId().intValue());
            prizeList.add(transferMap);
        });
        Integer anInt = integer.get("int");
        if (anInt == null) {
            anInt = 0;
        } else if (anInt == 0) {
            anInt = 1;
        }
        Random random = new Random(anInt);
        int obtainNum = random.nextInt();
        prizeList.forEach(map -> {
            Integer maxInt = map.get("maxInt");
            Integer minInt = map.get("minInt");
            if (obtainNum > minInt && obtainNum < maxInt) {
                integer.put("prizeId", map.get("prizeId"));
                return;
            }
        });
        return integer.get("prizeId");

    }
}
