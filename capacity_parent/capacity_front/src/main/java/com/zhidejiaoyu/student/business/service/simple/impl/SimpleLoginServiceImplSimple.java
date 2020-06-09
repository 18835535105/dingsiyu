package com.zhidejiaoyu.student.business.service.simple.impl;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.aliyunoss.getObject.GetOssFile;
import com.zhidejiaoyu.common.annotation.GoldChangeAnnotation;
import com.zhidejiaoyu.common.award.GoldAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.PetImageConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.GoldLogMapper;
import com.zhidejiaoyu.common.mapper.StudentExpansionMapper;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.Award;
import com.zhidejiaoyu.common.pojo.CapacityReview;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.DurationUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.LearnTimeUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.simple.SimpleLoginServiceSimple;
import com.zhidejiaoyu.student.common.GoldLogUtil;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登陆业务实现层
 *
 * @author qizhentao
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SimpleLoginServiceImplSimple extends SimpleBaseServiceImpl<SimpleStudentMapper, Student> implements SimpleLoginServiceSimple {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SimpleStudentMapper simpleStudentMapper;

    @Autowired
    private SimpleLearnMapper learnMapper;

    @Autowired
    private SimpleAwardMapper simpleAwardMapper;

    @Autowired
    private RedisOpt redisOpt;

    @Autowired
    private SimpleCapacityStudentUnitMapper simpleCapacityStudentUnitMapper;

    @Autowired
    private GoldAwardAsync goldAwardAsync;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Resource
    private GoldLogMapper goldLogMapper;

    @Resource
    private StudentExpansionMapper studentExpansionMapper;

    @Override
    @GoldChangeAnnotation
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<String> updatePassword(String oldPassword, String password, HttpSession session) {
        Student student = super.getStudent(session);
        Long studentId = student.getId();
        String account = student.getAccount();

        Integer state = simpleStudentMapper.updatePassword(account, password, studentId);
        if (state == 1) {
            student.setPassword(password);

            Award award = simpleAwardMapper.selectByAwardContentTypeAndType(studentId, 2, 12);
            if (award == null || award.getCanGet() == 2) {
                // 首次修改密码
                int gold = 10;
                student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), gold));
                GoldLogUtil.saveStudyGoldLog(student.getId(), "首次修改密码", gold);

                // 首次修改密码奖励
                goldAwardAsync.dailyAward(student, 12);
            }
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
            return ServerResponse.createBySuccessMessage("修改成功");
        } else {
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 单词首页数据
     */
    @Override
    public ServerResponse<Object> index(HttpSession session) {
        Student student = super.getStudent(session);
        // 学生id
        Long studentId = student.getId();

        // 封装返回数据
        Map<String, Object> result = new HashMap<>(16);

        // 学生id
        result.put("student_id", student.getId());
        // 课程包
        result.put("coursePackage", "课程中心");
        // 账号
        result.put("account", student.getAccount());
        // 昵称
        result.put("studentName", student.getStudentName());
        // 头像
        result.put("headUrl", GetOssFile.getPublicObjectUrl(student.getHeadUrl()));
        // 宠物
        result.put("partUrl", GetOssFile.getPublicObjectUrl(student.getPartUrl()));
        // 宠物名
        result.put("petName", student.getPetName());
        result.put("schoolName", student.getSchoolName());
        //性别
        if (1 == student.getSex()) {
            result.put("sex", "男");
        } else {
            result.put("sex", "女");
        }

        // 判断学生是否有智能版单词
        int count = simpleCapacityStudentUnitMapper.countByType(student, 1);
        result.put("hasCapacityWord", count > 0);

        // 有效时长  !
        int valid = (int) DurationUtil.getTodayValidTime(session);
        // 在线时长 !
        int online = (int) DurationUtil.getTodayOnlineTime(session);
        // 今日学习效率 !
        if (valid >= online) {
            logger.error("有效时长大于或等于在线时长：validTime=[{}], onlineTime=[{}], student=[{}]", valid, online, student);
            valid = online - 1;
            result.put("efficiency", "99%");
        } else {
            String efficiency = LearnTimeUtil.efficiency(valid, online);
            result.put("efficiency", efficiency);
        }
        result.put("online", online);
        result.put("valid", valid);

        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(studentId);
        // 获得的总代金券
        result.put("voucher", studentExpansion == null ? 0 : studentExpansion.getCashCoupon());

        for (int i = 1; i <= 9; i++) {
            Map<String, Object> resultMap = new HashMap<>(16);
            // 当前模块未开启
            resultMap.put("open", false);
            // 继续学习状态
            resultMap.put("state", 2);
            resultMap.put("sum", "0");
            resultMap.put("countWord", "0");
            // 速度
            resultMap.put("speed", "0");
            result.put(i + "", resultMap);
        }

        // 值得元老勋章
        medalAwardAsync.oldMan(student);

        return ServerResponse.createBySuccess(result);
    }

    /**
     * 点击头像,需要展示的信息
     * <p>
     * 今日已学单词/例句   date_format(learn_time, '%Y-%m-%d')
     * 总金币/今日金币
     * 我的等级/下一个等级
     * 距离下一级还差多少金币
     */
    @Override
    public ServerResponse<Object> clickPortrait(HttpSession session) {
        long studentId = super.getStudentId(session);
        Student student = getStudent(session);

        // 获取今日已学单词
        int learnWord = learnMapper.getTodayWord(DateUtil.formatYYYYMMDD(new Date()), studentId);
        // 获取今日已学例句
        int learnSentence = learnMapper.getTodaySentence(DateUtil.formatYYYYMMDD(new Date()), studentId);

        Map<String, Object> map = new HashMap<>(16);
        map.put("learnWord", learnWord);
        map.put("learnSentence", learnSentence);
        map.put("sex", student.getSex());
        // 获取我的总金币
        int myGold = (int) BigDecimalUtil.add(student.getSystemGold(), student.getOfflineGold());
        map.put("myGold", myGold);

        this.getMyLevelInfo(map, myGold, redisOpt);

        // 获取今日获得金币
        Integer todayGold = goldLogMapper.sumTodayAddGold(student.getId());
        map.put("myThisGold", todayGold == null ? 0 : todayGold);

        return ServerResponse.createBySuccess(map);
    }

    /**
     * 封装我的等级信息
     *
     * @param map
     * @param myGold
     */
    private void getMyLevelInfo(Map<String, Object> map, int myGold, RedisOpt redisOpt) {
        // 获取等级规则
        List<Map<String, Object>> levels = redisOpt.getAllLevel();

        int myrecord = 0;
        // 下一等级索引
        int j = 1;
        int size = levels.size();
        for (int i = 0; i < size; i++) {
            // 循环的当前等级分数
            int levelGold = (int) levels.get(i).get("gold");
            // 下一等级分数
            int nextLevelGold = (int) levels.get((i + 1) < levels.size() ? (i + 1) : i).get("gold");
            // 下一等级索引
            int si = (i + 1) < size ? (i + 1) : i;
            boolean flag = (myGold >= myrecord && myGold < nextLevelGold) || j == size;
            if (flag) {
                // 我的等级
                map.put("childName", levels.get(i).get("child_name"));
                // 距离下一等级还差多少金币
                map.put("jap", (nextLevelGold - myGold));
                // 我的等级图片
                map.put("imgUrl", AliyunInfoConst.host + levels.get(i).get("img_url"));
                // 下一个等级名/ 下一个等级需要多少金币 / 下一个等级图片
                // 下一级等级名
                map.put("childNameBelow", levels.get(si).get("child_name"));
                // 下一级金币数量
                map.put("japBelow", (nextLevelGold));
                // 下一级等级图片
                map.put("imgUrlBelow", AliyunInfoConst.host + levels.get(si).get("img_url"));
                break;
            }
            myrecord = levelGold;
            j++;
        }
    }

}
