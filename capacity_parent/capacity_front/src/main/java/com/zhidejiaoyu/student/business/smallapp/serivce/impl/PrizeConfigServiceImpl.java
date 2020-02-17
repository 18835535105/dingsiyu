package com.zhidejiaoyu.student.business.smallapp.serivce.impl;

import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.smallapp.serivce.PrizeConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class PrizeConfigServiceImpl extends BaseServiceImpl<PrizeConfigMapper, PrizeConfig> implements PrizeConfigService {

    @Resource
    private StudentPayConfigMapper studentPayConfigMapper;
    @Resource
    private PrizeConfigMapper prizeConfigMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private TestRecordMapper testRecordMapper;
    @Resource
    private ShareConfigMapper shareConfigMapper;
    private final String GENRE = "飞行测试";
    private final String STUDY_MODEL = "小程序测试";

    @Override
    public Object getPrizeConfig(String openId, Long adminId) {
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
            studentPayConfigMapper.insert(studentPayConfig);
        } else {
            payconfigId = studentPayConfig.getPrizeConfigId();
        }
        PrizeConfig prizeConfig = prizeConfigMapper.selectById(payconfigId);
        returnMap.put("prizeName", prizeConfig.getPrizeName());
        SysUser sysUser = sysUserMapper.selectById(adminId);
        returnMap.put("adminPhone", sysUser.getPhone());
        returnMap.put("obtain", studentPayConfig.getObtain());


        return null;
    }

    @Override
    public Object getAdmin(HttpSession session) {
        Student student = getStudent(session);
        Integer adminId = teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId());
        TestRecord testRecord = testRecordMapper.selectByStudentIdAndGenreAndStudyModel(student.getId(), GENRE, STUDY_MODEL);
        String imgUrl = shareConfigMapper.selectImgByAdminId(adminId);
        Map<String, Object> map = new HashMap<>();
        map.put("adminId", adminId);
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
