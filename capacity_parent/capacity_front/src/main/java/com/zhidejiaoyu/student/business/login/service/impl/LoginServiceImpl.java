package com.zhidejiaoyu.student.business.login.service.impl;

import com.zhidejiaoyu.aliyunoss.common.AliyunInfoConst;
import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.ServerNoConstant;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.constant.redis.RankKeysConst;
import com.zhidejiaoyu.common.constant.redis.RedisKeysConst;
import com.zhidejiaoyu.common.dto.student.SaveStudentInfoToCenterDTO;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.RankOpt;
import com.zhidejiaoyu.common.utils.*;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LocationUtil;
import com.zhidejiaoyu.common.utils.locationUtil.LongitudeAndLatitude;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.feignclient.center.UserInfoFeignClient;
import com.zhidejiaoyu.student.business.login.service.LoginService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipAddEquipmentService;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * @author: wuchenxi
 * @Date: 2019/12/2 15:40
 */
@Slf4j
@Service(value = "newLoginService")
public class LoginServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements LoginService {
    @Resource
    private StudentMapper studentMapper;

    @Resource
    private RunLogMapper runLogMapper;

    @Resource
    private DurationMapper durationMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StudentExpansionMapper studentExpansionMapper;

    @Resource
    private MedalAwardAsync medalAwardAsync;

    @Resource
    private DailyAwardAsync dailyAwardAsync;

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private JoinSchoolMapper joinSchoolMapper;

    @Resource
    private LocationMapper locationMapper;

    @Resource
    private ExecutorService executorService;

    @Resource
    private LevelMapper levelMapper;

    @Resource
    private LocationUtil locationUtil;

    @Resource
    private ShipAddEquipmentService shipAddEquipmentService;

    @Resource
    private RedisOpt redisOpt;

    @Resource
    private RankOpt rankOpt;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private UserInfoFeignClient userInfoFeignClient;

    /**
     * 账号关闭状态
     */
    private static final int CLOSE = 2;

    /**
     * 账号删除状态
     */
    private static final int DELETE = 3;

    /**
     * 账号冻结状态
     */
    private static final int FREEZE = 3;

    @Resource
    private TotalHistoryPlanMapper totalHistoryPlanMapper;

    @Resource
    private WeekHistoryPlanMapper weekHistoryPlanMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> loginJudge(String account, String password) {

        Student stu = this.getStudent(account, password);

        // 1.账号/密码错误
        if (stu == null) {
            log.warn("学生账号[{}]账号或密码输入错误，登录失败。", account);
            return ServerResponse.createByErrorMessage("账号或密码输入错误");
            // 2.账号已关闭
        } else if (stu.getStatus() != null && stu.getStatus() == CLOSE) {
            return ServerResponse.createByErrorMessage("此账号已关闭");

        } else if (stu.getStatus() != null && stu.getStatus() == DELETE) {
            // 账号被删除
            return ServerResponse.createByErrorMessage("此账号已被删除");
        } else if (stu.getStatus() != null && stu.getStatus() == FREEZE) {
            // 账号被冻结
            return ServerResponse.createByErrorMessage("账号已被冻结，请联系教管教师");
            // 3.正确
        } else {
            // 封装返回数据
            Map<String, Object> result = new HashMap<>(16);

            // 将登录的学生放入指定 key 用于统计在线人数
            redisTemplate.opsForZSet().add(RedisKeysConst.ZSET_ONLINE_USER, stu.getId(), 1);

            // 学生首次登陆系统，初始化其账号有效期，勋章信息
            initAccountTime(stu);

            // 账号有效期
            Date date = stu.getAccountTime();
            // 当前时间
            Date current = new Date();
            // 2.此账号已失效
            if (date == null || date.getTime() < current.getTime()) {
                return ServerResponse.createByErrorMessage("此账号已失效");
            }

            // 3.账号即将过期，请及时续期
            long difference = (date.getTime() - current.getTime()) / 86400000;
            long l = Math.abs(difference);
            if (l <= 1) {
                result.put("accountDate", "账号即将过期，请及时续期");
            }

            // 学生id
            result.put("student_id", stu.getId());
            // 账号
            result.put("account", stu.getAccount());
            // 姓名
            result.put("studentName", stu.getStudentName());
            // 头像
            result.put("headUrl", AliyunInfoConst.host + stu.getHeadUrl());
            result.put("schoolName", stu.getSchoolName());

            // 每日首次登陆需要初始化的内容
            initFirstLogin(stu);

            // 一个账户只能登陆一台
            judgeMultipleLogin(stu);
            addUnclock(stu);
            getStudentEqument(stu);
            executorService.execute(() -> {
                // 记录登录信息
                String ip = this.saveLoginRunLog(stu);
                // 判断学生是否是在加盟校半径 1 公里外登录
                this.isOtherLocation(stu, ip);

                this.saveUserInfoToCenterServer(stu);
            });
            // 2.判断是否需要完善个人信息
            if (!StringUtils.isNotBlank(stu.getHeadUrl())) {

                // 学校
                result.put("school_name", stu.getSchoolName());
                // 到期时间
                result.put("account_time", stu.getAccountTime());

                // 2.1 跳到完善个人信息页面
                log.info("学生[{} -> {} -> {}]登录成功前往完善信息页面。", stu.getId(), stu.getAccount(), stu.getStudentName());
                return ServerResponse.createBySuccess("2", result);
            }


            // 正常登陆
            log.info("学生[{} -> {} -> {}]登录成功。", stu.getId(), stu.getAccount(), stu.getStudentName());
            return ServerResponse.createBySuccess("1", result);
        }

    }

    /**
     * 保存学生信息到中台服务器
     *
     * @param student
     */
    private void saveUserInfoToCenterServer(Student student) {
        SaveStudentInfoToCenterDTO saveStudentInfoToCenterDTO = new SaveStudentInfoToCenterDTO();
        saveStudentInfoToCenterDTO.setServerNo(ServerNoConstant.SERVER_NO);
        saveStudentInfoToCenterDTO.setAccount(student.getAccount());
        saveStudentInfoToCenterDTO.setOpenid(student.getOpenid());
        saveStudentInfoToCenterDTO.setPassword(student.getPassword());
        saveStudentInfoToCenterDTO.setUuid(student.getUuid());
        userInfoFeignClient.saveUserInfo(saveStudentInfoToCenterDTO);
    }

    private void getStudentEqument(Student stu) {
        List<Map<String, Object>> returnList = new ArrayList<>();
        List<Long> addIdList = new ArrayList<>();
        shipAddEquipmentService.getStudentEqu(stu, returnList, addIdList, 2);
    }

    private void addUnclock(Student stu) {
        TotalHistoryPlan totalHistoryPlan = totalHistoryPlanMapper.selectByStudentId(stu.getId());
        if (totalHistoryPlan == null) {
            totalHistoryPlan = new TotalHistoryPlan();
            totalHistoryPlan.setStudentId(stu.getId());
            totalHistoryPlan.setTotalPoint(0);
            totalHistoryPlan.setTotalOnlineTime(0L);
            totalHistoryPlan.setTotalWord(0);
            totalHistoryPlan.setTotalVaildTime(0L);
            totalHistoryPlanMapper.insert(totalHistoryPlan);
        }
        WeekHistoryPlan weekHistoryPlan = weekHistoryPlanMapper.selectByTimeAndStudentId(DateUtil.formatYYYYMMDDHHMMSS(new Date()), stu.getId());
        if (weekHistoryPlan == null) {
            weekHistoryPlan = new WeekHistoryPlan();
            weekHistoryPlan.setValidTime(0L);
            weekHistoryPlan.setWord(0);
            weekHistoryPlan.setOnlineTime(0L);
            weekHistoryPlan.setPoint(0);
            weekHistoryPlan.setStudentId(stu.getId());
            weekHistoryPlan.setEndTime(DateUtil.maxTime(DateUtil.getWeekEnd()));
            weekHistoryPlan.setStartTime(DateUtil.minTime(DateUtil.getWeekStart()));
            weekHistoryPlanMapper.insert(weekHistoryPlan);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDurationInfo(Map<String, Object> sessionMap) {
        if (sessionMap != null) {
            Student student = (Student) sessionMap.get(UserConstant.CURRENT_STUDENT);
            Date loginTime = DateUtil.parseYYYYMMDDHHMMSS((Date) sessionMap.get(TimeConstant.LOGIN_TIME));
            Date loginOutTime = DateUtil.parseYYYYMMDDHHMMSS(new Date());
            // 清除学生的登录信息
            redisTemplate.opsForHash().delete(RedisKeysConst.LOGIN_SESSION, student.getId());
            if (loginTime != null && loginOutTime != null) {
                // 判断当前登录时间是否已经记录有在线时长信息，如果没有插入记录，如果有无操作
                int count = durationMapper.countOnlineTimeWithLoginTime(student, loginTime);
                if (count == 0) {
                    Duration duration = new Duration();
                    duration.setStudentId(student.getId());
                    duration.setOnlineTime(DurationUtil.getOnlineTimeBetweenThisAndLast(student, (Date) sessionMap.get(TimeConstant.LOGIN_TIME)));
                    duration.setLoginTime(loginTime);
                    duration.setLoginOutTime(loginOutTime);
                    duration.setValidTime(0L);
                    durationMapper.insert(duration);
                } else {
                    log.warn("学生[{} -{} - {}]已保存过登录时间为[{}]的在线时长信息！count=[{}]", student.getId(), student.getAccount(),
                            student.getStudentName(), DateTimeFormat.forPattern(DateUtil.YYYYMMDDHHMMSS).print(loginTime.getTime()), count);
                }
            } else {
                log.warn("学生[{} -{} - {}]的登录时间信息为空！", student.getId(), student.getAccount(), student.getStudentName());
            }
        }
    }

    @Override
    public Object isLoginOut(HttpSession session, String isTeacherAccount) {
        Map<String, Object> map = new HashMap<>(16);
        Student student = getStudent(session);
        if (student.getTeacherId().equals(1L)) {
            map.put("isLoginOut", "admin".equals(isTeacherAccount.trim()));
            return ServerResponse.createBySuccess(map);
        }
        String adminAccount = null;
        boolean flag = false;
        if (student.getTeacherId() != null) {
            SysUser sysUser = sysUserMapper.selectById(student.getTeacherId());
            if (sysUser.getAccount().contains("xg")) {
                adminAccount = sysUser.getAccount();
            } else {
                if (Objects.equals(sysUser.getAccount(), isTeacherAccount.trim())) {
                    flag = true;
                }
                if (!flag) {
                    Integer schoolAdminId = teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId());
                    if (schoolAdminId != null) {
                        SysUser schoolAdminUser = sysUserMapper.selectById(schoolAdminId);
                        if (schoolAdminUser != null) {
                            adminAccount = schoolAdminUser.getAccount();
                        }
                    }
                }
            }
        }
        if (adminAccount != null && adminAccount.equals(isTeacherAccount.trim())) {
            flag = true;
        }
        map.put("isLoginOut", flag);
        return ServerResponse.createBySuccess(map);
    }

    /**
     * @param password    新密码
     * @param session
     * @param oldPassword 旧密码
     * @param studentId   对比 学生id
     * @return
     */
    @Override
    public ServerResponse<String> updatePassword(String password, HttpSession session, String oldPassword, Long studentId) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (!Objects.equals(student.getId(), studentId)) {
            log.error("学生 {}->{} 试图修改学生 {} 的密码！", student.getId(), student.getStudentName(), studentId);
            return ServerResponse.createByErrorMessage("无权限修改他人密码！");
        }
        if (!Objects.equals(student.getPassword(), oldPassword)) {
            return ServerResponse.createByErrorMessage("原密码输入错误！");
        }
        student.setPassword(password);
        int state = studentMapper.updateById(student);
        if (state == 1) {
            session.setAttribute(UserConstant.CURRENT_STUDENT, student);
            return ServerResponse.createBySuccessMessage("修改成功");
        } else {
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 判断学生是否是在加盟校半径 1 公里外登录
     *
     * @param stu
     * @param ip  学生登录的 ip 地址
     */
    private void isOtherLocation(Student stu, String ip) {
        JoinSchool joinSchool = this.getJoinSchool(stu, ip);
        if (joinSchool == null) {
            return;
        }

        // 校验距离
        this.checkDistance(stu, ip, joinSchool);
    }

    private void checkDistance(Student stu, String ip, JoinSchool joinSchool) {
        LongitudeAndLatitude longitudeAndLatitude = locationUtil.getLongitudeAndLatitude(ip);

        LongitudeAndLatitude schoolLongitudeAndLatitude = new LongitudeAndLatitude();
        schoolLongitudeAndLatitude.setLatitude(joinSchool.getLatitude());
        schoolLongitudeAndLatitude.setLongitude(joinSchool.getLongitude());

        int distance = locationUtil.getDistance(longitudeAndLatitude, schoolLongitudeAndLatitude);
        // 学生距加盟校的最远距离
        final int radius = 1000;
        if (distance > radius) {
            log.warn("学生 [{} - {} - {}] 登录距离距加盟校 [{}] 超过 [{}] 米！", stu.getId(), stu.getAccount(), stu.getStudentName(), joinSchool.getSchoolName(), radius);
            Date date = new Date();
            // 学生在加盟校 1000 米之外登录，保存异地登录记录
            Location location = new Location();
            location.setAccount(stu.getAccount());
            location.setCreateTime(date);
            location.setIp(ip);
            location.setLocation(longitudeAndLatitude.getAddresses());
            location.setStudentId(stu.getId());
            location.setUpdateTime(date);
            try {
                locationMapper.insert(location);
            } catch (Exception e) {
                log.error("保存学生 [{} - {} - {}]异地登录信息失败！", stu.getId(), stu.getAccount(), stu.getStudentName(), e);
            }
        }
    }

    /**
     * 获取加盟校信息并做校验
     *
     * @param stu
     * @param ip
     * @return
     */
    private JoinSchool getJoinSchool(Student stu, String ip) {
        if (stu.getTeacherId() == null) {
            log.warn("学生 [{} - {} - {}] 没有所属教师！", stu.getId(), stu.getAccount(), stu.getStudentName());
            return null;
        }

        if (StringUtils.isEmpty(ip)) {
            log.warn("没有获取到学生 [{} - {} - {}] 的登录 ip", stu.getId(), stu.getAccount(), stu.getStudentName());
            return null;
        }

        // 学生的校管 id
        Integer schoolAdminId = teacherMapper.selectSchoolAdminIdByTeacherId(stu.getTeacherId());
        if (schoolAdminId == null) {
            schoolAdminId = Integer.parseInt(stu.getTeacherId().toString());
        }

        // 查询学生所属加盟校地址
        JoinSchool joinSchool = joinSchoolMapper.selectByUserId(schoolAdminId);

        if (joinSchool == null) {
            log.warn("没有获取到学生 [{} - {} - {}] 所属的加盟校信息！", stu.getId(), stu.getAccount(), stu.getStudentName());
            return null;
        }

        if (StringUtils.isEmpty(joinSchool.getLatitude()) || StringUtils.isEmpty(joinSchool.getLongitude())) {
            log.warn("学生 [{} - {} - {}] 所属的加盟校信息中没有坐标信息！", stu.getId(), stu.getAccount(), stu.getStudentName());
            return null;
        }
        return joinSchool;
    }

    private String saveLoginRunLog(Student stu) {
        String ip = null;
        try {
            ip = MacIpUtil.getIpAddr(HttpUtil.getHttpServletRequest());
        } catch (Exception e) {
            log.error("获取学生登录IP地址出错，error=[{}]", e.getMessage());
        }

        try {
            super.saveRunLog(stu, 1, "学生[" + stu.getStudentName() + "]登录,ip=[" + ip + "]");
        } catch (Exception e) {
            log.error("学生 {} -> {} 登录信息记录失败！ExceptionMsg:{}", stu.getId(), stu.getStudentName(), e.getMessage(), e);
        }
        return ip;
    }

    /**
     * 每日首次登陆需要初始化的内容
     *
     * @param stu
     */
    private void initFirstLogin(Student stu) {
        int count = runLogMapper.countStudentTodayLogin(stu);
        if (count <= 1) {
            executorService.execute(() -> {

                // 招生账号每日首次登陆初始化 50 个能量供体验抽奖
                this.addEnergy(stu);

                // 每日首次登陆日奖励
                dailyAwardAsync.firstLogin(stu);

                // 当天首次登陆将学生每日闯关获取金币数清空
                this.resetTestGold(stu);

                // 如果昨天辉煌荣耀奖励没有更新，将指定的奖励当前进度置为0
                medalAwardAsync.updateHonour(stu);

                // 将天道酬勤中未达到领取条件的勋章重置
                medalAwardAsync.resetLevel(stu);

                // 资深队员勋章
                medalAwardAsync.oldMan(stu);
            });
        }
    }

    private void resetTestGold(Student stu) {
        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(stu.getId());
        if (studentExpansion == null) {
            studentExpansion = new StudentExpansion();
            studentExpansion.setStudentId(stu.getId());
            studentExpansion.setTestGoldAdd(0);
            try {
                studentExpansionMapper.insert(studentExpansion);
            } catch (Exception e) {
                log.error("当日首次登陆重置学生闯关类获取金币数量失败[{}]->[{}]", stu.getId(), stu.getStudentName(), e);
            }
        } else {
            studentExpansion.setTestGoldAdd(0);
            try {
                studentExpansionMapper.updateById(studentExpansion);
            } catch (Exception e) {
                log.error("当日首次登陆重置学生闯关类获取金币数量失败[{}]->[{}]", stu.getId(), stu.getStudentName(), e);
            }
        }
    }

    /**
     * 招生账号每日首次登陆初始化 50 个能量供体验抽奖
     *
     * @param stu
     */
    private void addEnergy(Student stu) {
        // 是招生账号
        int welfareAccount = 4;
        if (Objects.equals(stu.getRole(), welfareAccount)) {
            stu.setEnergy(50);
            this.studentMapper.updateById(stu);
        }
    }

    /**
     * 学生首次登陆系统初始化其账号有效期
     *
     * @param student
     */
    private void initAccountTime(Student student) {
        if (student.getAccountTime() == null) {
            student.setAccountTime(new Date(System.currentTimeMillis() + student.getRank() * 24 * 60 * 60 * 1000L));
            if (StringUtil.isEmpty(student.getUuid())) {
                student.setUuid(IdUtil.getId());
            }
            studentMapper.updateById(student);

            executorService.execute(() -> {
                // 初始化学生勋章信息
                dailyAwardAsync.initAward(student);

                // 初始化学生排行数据
                initRankInfo(student);

                initStudentExpansion(student);
            });
        } else if (StringUtil.isEmpty(student.getUuid())) {
            student.setUuid(IdUtil.getId());
            studentMapper.updateById(student);
        }
    }

    private Student getStudent(String account, String password) {
        Student st = new Student();
        st.setAccount(account);
        st.setPassword(password);
        return studentMapper.loginJudge(st);
    }

    private void judgeMultipleLogin(Student stu) {
        HttpSession session = HttpUtil.getHttpSession();
        Object oldSessionIdObject = redisTemplate.opsForHash().get(RedisKeysConst.LOGIN_SESSION, stu.getId());
        if (oldSessionIdObject != null) {
            Long oldStudentId = null;
            String oldSessionId = oldSessionIdObject.toString();
            Map<String, Object> oldSessionMap = RedisOpt.getSessionMap(oldSessionId);
            if (oldSessionMap != null && oldSessionMap.get(UserConstant.CURRENT_STUDENT) != null) {
                oldStudentId = ((Student) oldSessionMap.get(UserConstant.CURRENT_STUDENT)).getId();
            }

            // 如果账号 session 相同说明是同一个浏览器中，并且不是同一个账号，不再更改其 session 中登录信息
            if (Objects.equals(oldStudentId, stu.getId()) && Objects.equals(oldSessionIdObject, HttpUtil.getHttpSession().getId())) {
                log.warn("学生[{} -{} -{}]在同一浏览器打开多个系统页面！", stu.getId(), stu.getAccount(), stu.getStudentName());
                return;
            }

            // 如果账号登录的session不同，保存前一个session的信息
            if (oldSessionMap != null) {
                log.warn("学生[{} -{} -{}]在不同浏览器登录！", stu.getId(), stu.getAccount(), stu.getStudentName());
                saveDurationInfo(oldSessionMap);
                saveLogoutLog(stu, runLogMapper, log);
                redisOpt.markMultipleLoginSessionId(oldSessionId);
            }
        }

        // 学生首次在当前浏览器登录时记录其登录信息
        Date loginTime = DateUtil.parseYYYYMMDDHHMMSS(new Date());
        session.setAttribute(UserConstant.CURRENT_STUDENT, stu);
        session.setAttribute(TimeConstant.LOGIN_TIME, loginTime);

        Map<String, Object> sessionMap = new HashMap<>(16);
        sessionMap.put(UserConstant.CURRENT_STUDENT, stu);
        sessionMap.put(TimeConstant.LOGIN_TIME, loginTime);
        sessionMap.put("sessionId", session.getId());

        redisTemplate.opsForHash().put(RedisKeysConst.SESSION_MAP, session.getId(), sessionMap);
        redisTemplate.opsForHash().put(RedisKeysConst.LOGIN_SESSION, stu.getId(), session.getId());
    }

    public static void saveLogoutLog(Student student, RunLogMapper runLogMapper, Logger logger) {
        // 查询学生登录日志中最后一条记录时登录信息还是退出信息
        RunLog lastRunLog = runLogMapper.selectLastRunLogByOperateUserId(student.getId());
        if (lastRunLog == null || !lastRunLog.getLogContent().contains("退出登录")) {
            RunLog runLog = new RunLog(student.getId(), 1, "学生[" + student.getStudentName() + "]退出登录", new Date());
            try {
                runLogMapper.insert(runLog);
            } catch (Exception e) {
                logger.error("记录学生 [{}]->[{}] 退出登录信息失败！", student.getId(), student.getStudentName(), e);
            }
        }
    }

    /**
     * 初始化学生排行数据
     *
     * @param student
     */
    private void initRankInfo(Student student) {
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);

        double goldCount = BigDecimalUtil.add(student.getOfflineGold(), student.getSystemGold());
        rankOpt.addOrUpdate(RankKeysConst.CLASS_GOLD_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), goldCount);
        rankOpt.addOrUpdate(RankKeysConst.SCHOOL_GOLD_RANK + schoolAdminId, student.getId(), goldCount);
        rankOpt.addOrUpdate(RankKeysConst.SERVER_GOLD_RANK, student.getId(), goldCount);
        rankOpt.addOrUpdate(RankKeysConst.COUNTRY_GOLD_RANK, student.getId(), goldCount);

        rankOpt.addOrUpdate(RankKeysConst.CLASS_CCIE_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.SCHOOL_CCIE_RANK + schoolAdminId, student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.SERVER_CCIE_RANK, student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.COUNTRY_CCIE_RANK, student.getId(), 0.0);

        rankOpt.addOrUpdate(RankKeysConst.CLASS_MEDAL_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.SCHOOL_MEDAL_RANK + schoolAdminId, student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.SERVER_MEDAL_RANK, student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.COUNTRY_MEDAL_RANK, student.getId(), 0.0);

        rankOpt.addOrUpdate(RankKeysConst.CLASS_WORSHIP_RANK + student.getTeacherId() + ":" + student.getClassId(), student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.SCHOOL_WORSHIP_RANK + schoolAdminId, student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.SERVER_WORSHIP_RANK, student.getId(), 0.0);
        rankOpt.addOrUpdate(RankKeysConst.COUNTRY_WORSHIP_RANK, student.getId(), 0.0);
    }

    /**
     * 判断扩展表信息是否已有  如果没有添加
     */
    private void initStudentExpansion(Student student) {
        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(student.getId());
        if (studentExpansion == null) {
            List<Map<String, Object>> levels = redisOpt.getAllLevel();
            double gold = student.getSystemGold() + student.getOfflineGold();
            int level = super.getLevel((int) gold, levels);
            Integer study = levelMapper.getStudyById(level);
            studentExpansion = new StudentExpansion();
            studentExpansion.setStudentId(student.getId());
            studentExpansion.setAudioStatus(1);
            studentExpansion.setStudyPower(study);
            studentExpansion.setLevel(level);
            studentExpansion.setIsLook(2);
            studentExpansion.setPkExplain(1);
            studentExpansion.setSourcePower(0);
            studentExpansionMapper.insert(studentExpansion);
        }
    }


}
