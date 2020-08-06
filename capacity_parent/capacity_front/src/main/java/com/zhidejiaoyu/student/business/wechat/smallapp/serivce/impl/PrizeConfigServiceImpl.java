package com.zhidejiaoyu.student.business.wechat.smallapp.serivce.impl;

import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.constant.test.StudyModelConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.mapper.center.WeChatMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.pojo.center.WeChat;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.wechat.smallapp.serivce.PrizeConfigService;
import com.zhidejiaoyu.student.business.wechat.smallapp.vo.ReturnAdminVo;
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
    @Resource
    private DurationMapper durationMapper;
    @Resource
    private LearnNewMapper learnNewMapper;

    @Override
    public Object getPrizeConfig(String openId, Long adminId, Long studentId, String weChatimgUrl, String weChatName) {
        //判断当前openId是否已经领取过今日的奖品
        Date date = new Date();
        Map<String, Object> returnMap = new HashMap<>();
        Long payconfigId = 0L;
        StudentPayConfig studentPayConfig = studentPayConfigMapper.selectByWenXiIdAndDate(openId, date);
        Student student = studentMapper.selectById(studentId);
        List<PrizeConfig> adminPrizeConfigs = prizeConfigMapper.selectByAdminId(1L);
        PrizeConfig adminPrizeConfig = adminPrizeConfigs.get(1);
        if (studentPayConfig == null) {
            //获取学校奖品数据
            List<PrizeConfig> prizeConfigs = prizeConfigMapper.selectByAdminId(adminId);
            if (prizeConfigs.size() > 0) {
                payconfigId = this.getPrize(prizeConfigs).longValue();
            } else {
                payconfigId = adminPrizeConfig.getId();
            }
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
        if (payconfigId > 0) {
            PrizeConfig prizeConfig = prizeConfigMapper.selectById(payconfigId);
            returnMap.put("prizeName", prizeConfig.getPrizeCount() > 0 ? prizeConfig.getPrizeName() : "5元优惠卷");
            prizeConfig.setPrizeCount(prizeConfig.getPrizeCount() - 1);
            prizeConfigMapper.updateById(prizeConfig);
        } else {
            returnMap.put("prizeName", "5元优惠卷");
        }

        JoinSchool joinSchool = joinSchoolMapper.selectByUserId(adminId.intValue());
        returnMap.put("adress", joinSchool == null ? "北京市海淀区上地国际创业园" : joinSchool.getAddress());
        SysUser sysUser = sysUserMapper.selectById(adminId);
        StringBuilder sb = new StringBuilder().append(sysUser.getPhone()).append("（").append(sysUser.getName(), 0, 1).append("老师）").append("-");
        SysUser teacherUser = sysUserMapper.selectById(student.getTeacherId());
        if (teacherUser.getAccount().contains("js")) {
            sb.append(sysUser.getPhone()).append("（").append(sysUser.getName(), 0, 1).append("老师）");
        }
        Teacher teacher = teacherMapper.selectTeacherBySchoolAdminId(adminId.intValue());
        returnMap.put("adminPhone", sb.toString());
        ShareConfig shareConfig = shareConfigMapper.selectByAdminId(adminId.intValue());
        if (shareConfig != null) {
            returnMap.put("background", GetOssFile.getPublicObjectUrl(shareConfig.getImgUrl()));
        } else {
            returnMap.put("background", null);
        }
        returnMap.put("obtain", studentPayConfig == null ? "谢谢参与" : studentPayConfig.getObtain());
        returnMap.put("campus", teacher.getSchool().replace("体验中心", "校区"));
        return ServerResponse.createBySuccess(returnMap);
    }

    @Override
    public ServerResponse<ReturnAdminVo> getAdmin(String openId) {
        Student student = studentMapper.selectByOpenId(openId);
        Integer adminId = teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId());
        TestRecord testRecord = testRecordMapper.selectByStudentIdAndGenreAndStudyModel(student.getId(), GenreConstant.SMALLAPP_GENRE, StudyModelConstant.SMALLAPP_STUDY_MODEL);
        String imgUrl = shareConfigMapper.selectImgByAdminId(adminId);
        Long vaildTime = durationMapper.selectTotalValidTimeByStudentId(student.getId());
        int wordCount = learnNewMapper.countLearnedWordCount(student.getId());
        ReturnAdminVo returnAdminVo = ReturnAdminVo.builder()
                .adminId(adminId.longValue())
                .gold(student.getSystemGold().intValue() + student.getOfflineGold().intValue())
                .vaildTime(vaildTime)
                .headPortrait(GetOssFile.getPublicObjectUrl(student.getHeadUrl()))
                .imgUrl(imgUrl)
                .learnTime(DateUtil.formatDate(testRecord.getTestEndTime(), DateUtil.YYYYMMDDYEAR))
                .wordCount(wordCount)
                .weChatList(studentPayConfigMapper.selectWeChatNameAndWeChatImgUrlByStudentId(student.getId()))
                .point(testRecord.getPoint())
                .studentId(student.getId())
                .studentName(student.getNickname())
                .build();
        return ServerResponse.createBySuccess(returnAdminVo);
    }

    private Integer getPrize(List<PrizeConfig> prizeConfigs) {
        List<Map<String, Integer>> prizeList = new ArrayList<>();
        Map<String, Integer> integer = new HashMap<>();
        prizeConfigs.forEach(prize -> {
            double dou = (prize.getChance() * 100);
            int addInt = (int) dou;
            Integer anInt = integer.get("int");
            int minInt;
            int maxInt;
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
        Random random = new Random();
        int obtainNum = random.nextInt(anInt);
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
