package com.zhidejiaoyu.student.business.wechat.smallapp.serivce.impl;

import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.constant.test.StudyModelConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.wechat.smallapp.serivce.PrizeConfigService;
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
    @Resource
    private JoinSchoolMapper joinSchoolMapper;

    @Override
    public Object getPrizeConfig(String openId, Long adminId, Long studentId, String weChatimgUrl, String weChatName) {
        //判断当前openId是否已经领取过今日的奖品
        Date date = new Date();
        Map<String, Object> returnMap = new HashMap<>();
        Long payconfigId;
        StudentPayConfig studentPayConfig = studentPayConfigMapper.selectByWenXiIdAndDate(openId, date);
        Student student = studentMapper.selectById(studentId);
        if (studentPayConfig == null) {
            //获取学校奖品数据
            List<PrizeConfig> prizeConfigs = prizeConfigMapper.selectByAdminId(adminId);
            payconfigId = this.getPrize(prizeConfigs).longValue();
            //获取图片
            studentPayConfig = new StudentPayConfig()
                    .setCreateTime(date)
                    .setPrizeConfigId(payconfigId)
                    .setWenXinId(openId)
                    .setObtain("" + date.getTime() + new Random(1000).nextInt())
                    .setWeChatImgUrl(weChatimgUrl)
                    .setWeChatName(weChatName)
                    .setStudentId(studentId);
            studentPayConfigMapper.insert(studentPayConfig);
            student.setSystemGold(student.getSystemGold() + 5);
            studentMapper.updateById(student);
        } else {
            payconfigId = studentPayConfig.getPrizeConfigId();
        }
        PrizeConfig prizeConfig = prizeConfigMapper.selectById(payconfigId);
        returnMap.put("prizeName", prizeConfig.getPrizeCount() > 0 ? prizeConfig.getPrizeName() : "谢谢惠顾");
        prizeConfig.setPrizeCount(prizeConfig.getPrizeCount() - 1);
        prizeConfigMapper.updateById(prizeConfig);
        JoinSchool joinSchool = joinSchoolMapper.selectByUserId(adminId.intValue());
        returnMap.put("adress", joinSchool.getAddress());
        SysUser sysUser = sysUserMapper.selectById(adminId);
        StringBuilder sb = new StringBuilder().append(sysUser.getPhone()).append("（").append(sysUser.getName().substring(0, 1)).append("老师）");
        SysUser teacherUser = sysUserMapper.selectById(student.getTeacherId());
        if (teacherUser.getAccount().contains("js")) {
            sb.append("$&$").append(sysUser.getPhone()).append("（").append(sysUser.getName().substring(0, 1)).append("老师）");
        }
        Teacher teacher = teacherMapper.selectTeacherBySchoolAdminId(adminId.intValue());
        returnMap.put("adminPhone", sb.toString());
        ShareConfig shareConfig = shareConfigMapper.selectByAdminId(adminId.intValue());
        if (shareConfig != null) {
            returnMap.put("background", shareConfig.getImgUrl());
        } else {
            returnMap.put("background", null);
        }
        returnMap.put("obtain", studentPayConfig.getObtain());
        returnMap.put("campus", teacher.getSchool().replace("体验中心", "校区"));
        return returnMap;
    }

    @Override
    public Object getAdmin(String openId) {
        Student student = studentMapper.selectByOpenId(openId);
        Integer adminId = teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId());
        TestRecord testRecord = testRecordMapper.selectByStudentIdAndGenreAndStudyModel(student.getId(), GenreConstant.SMALLAPP_GENRE, StudyModelConstant.SMALLAPP_STUDY_MODEL);
        String imgUrl = shareConfigMapper.selectImgByAdminId(adminId);
        Map<String, Object> map = new HashMap<>();
        map.put("adminId", adminId);
        map.put("weChatList", studentPayConfigMapper.selectWeChatNameAndWeChatImgUrlByStudentId(student.getId()));
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