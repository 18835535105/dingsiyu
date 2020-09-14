package com.dfdz.teacher.business.payCard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.teacher.business.course.service.CourseService;
import com.dfdz.teacher.business.payCard.service.PayCardService;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.dto.wechat.qy.teacher.PayStudentsDTO;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.RankOpt;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class PayCardServiceImpl extends ServiceImpl<PayCardMapper, PayCard> implements PayCardService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SchoolHoursMapper schoolHoursMapper;
    @Resource
    private StudentMapper studentMapper;
    @Resource
    private StudentExpansionMapper studentExpansionMapper;
    @Resource
    private StudentHoursMapper studentHoursMapper;
    @Resource
    private PayLogMapper payLogMapper;
    @Resource
    private CourseService courseService;
    @Resource
    private TestRecordMapper testRecordMapper;
    @Resource
    private RecycleBinMapper recycleBinMapper;
    @Resource
    private RankOpt rankOpt;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object pay(String studentUUID, Integer months, String openId) {
        SysUser user = sysUserMapper.selectByOpenId(openId);
        // 查询当前人员拥有的充值卡个数
        SchoolHours schoolHours = schoolHoursMapper.selectByAdminId(user.getId());
        Map<String, Object> flag = new HashMap<>();
        flag.put("index", 1);
        Student student = studentMapper.selectByUuid(studentUUID);
        if (!user.getAccount().contains("admin")) {
            flag = getType(months, Integer.parseInt(schoolHours.getCaptainCoin()), student.getId());
        }
        addCourse(student);
        Date now = new Date();
        Integer studentTime = getStudentTime(student, months);
        Map<String, String> map = new HashMap<>();
        Integer index = Integer.parseInt(flag.get("index").toString());
        if (!index.equals(1)) {
            if (index.equals(2)) {
                return ServerResponse.createByError(500, flag.get("message").toString());
            }
            if (index.equals(3)) {
                return ServerResponse.createByError(500, flag.get("message").toString());
            }
        }
        if (user.getAccount().contains("admin")) {
            updStudent(student, studentTime);
            addPayCard(user, now, student, months, map);

            addPayLog(student, studentTime, now, null, user);
            return ServerResponse.createBySuccess();
        } else {
            if (index.equals(1)) {
                //为学生添加
                addPays(user, now, student, months, studentTime,
                        Integer.parseInt(flag.get("useCaptainCoin").toString()));
                return ServerResponse.createBySuccess();
            }
            return ServerResponse.createByError(300, "无充课");
        }
    }

    private void addCourse(Student student) {
        //判断是否需要添加课程
        TestRecord testRecord = testRecordMapper.selectByGenre(student.getId(), GenreConstant.TEST_BEFORE_STUDY);
        if (testRecord != null) {
            getStudentUnit(student);
        }
    }

    @Override
    public Object addAllStudent(PayStudentsDTO dto) {
        String openId = dto.getOpenId();
        List<String> studentIds = dto.getStudentIds();
        Integer months = dto.getType();
        SysUser user = sysUserMapper.selectByOpenId(openId);
        StringBuilder sb = new StringBuilder();
        for (String studentId : studentIds) {
            // 查询当前人员拥有的充值卡个数
            SchoolHours schoolHours = schoolHoursMapper.selectByAdminId(user.getId());
            Map<String, Object> flag = new HashMap<>();
            flag.put("index", 1);
            Student student = studentMapper.selectByUuid(studentId);
            if (!user.getAccount().contains("admin")) {
                flag = getType(months, Integer.parseInt(schoolHours.getCaptainCoin()), student.getId());
            }
            //判断是否需要添加课程
            addCourse(student);
            Date now = new Date();
            Integer studentTime = getStudentTime(student, months);
            Map<String, String> map = new HashMap<>();
            Integer index = Integer.parseInt(flag.get("index").toString());
            if (!index.equals(1)) {
                if (index.equals(2)) {
                    sb.append("学生").append(student.getAccount()).append("添加课时因队长币不够未成功;");
                }
                if (index.equals(3)) {
                    sb.append("学生").append(student.getAccount()).append("添加课时因队长币不够未成功;");

                }
            } else {
                if (user.getAccount().contains("admin")) {
                    updStudent(student, studentTime);
                    addPayCard(user, now, student, months, map);
                    addPayLog(student, studentTime, now, null, user);
                } else {
                    //为学生添加
                    addPays(user, now, student, months, studentTime,
                            Integer.parseInt(flag.get("useCaptainCoin").toString()));
                }
            }
        }
        String str = sb.toString();
        if (str.length() > 2) {
            return ServerResponse.createByError(500, str);
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Object> getCapacityCoin(String openId) {
        SysUser sysUser = sysUserMapper.selectByOpenId(openId);
        SchoolHours schoolHours = schoolHoursMapper.selectByAdminId(sysUser.getId());
        return ServerResponse.createBySuccess(schoolHours == null ? 0 : schoolHours.getCaptainCoin());
    }


    /**
     * 为学生添加课时
     *
     * @param user        校管
     * @param now         时间
     * @param student     学生
     * @param months      充值月数
     * @param studentTime 充课天数
     */
    public void addPays(SysUser user, Date now, Student student, Integer months, Integer studentTime, Integer captainCoin) {
        //为学生添加课时
        Map<String, String> map = new HashMap<>();
        updStudent(student, studentTime);
        updSchoolHoursIsPay(captainCoin, user);
        addPayCard(user, now, student, months, map);
        String cardNos = map.get("cardNos");
        addPayLog(student, studentTime, now, cardNos, user);
    }

    /**
     * 给学生充课去除校管下课时信息
     *
     * @param captainCoins 学习币
     * @param user
     */
    private void updSchoolHoursIsPay(Integer captainCoins, SysUser user) {

        SchoolHours schoolHours = schoolHoursMapper.selectByAdminId(user.getId());
        int captainCoin = Integer.parseInt(schoolHours.getCaptainCoin()) - captainCoins;
        schoolHours.setCaptainCoin(Integer.toString(captainCoin));
        schoolHoursMapper.updateById(schoolHours);
    }

    /**
     * 添加log记录
     *
     * @param student 学生
     * @param time    充值卡信息
     * @param now     时间
     */
    private void addPayLog(Student student, Integer time, Date now, String cardType, SysUser user) {
        PayLog payLog = new PayLog();
        payLog.setStudentId(student.getId());
        payLog.setCardDate(time);
        payLog.setFoundDate(now);
        if (cardType == null) {
            payLog.setCardNo(getCardNo());
        } else {
            payLog.setCardNo(cardType);
        }
        payLog.setRecharge(now);
        payLog.setOperatorId(user.getId());
        payLogMapper.insert(payLog);
    }

    /**
     * 生成充值卡账号
     *
     * @return
     */
    private String getCardNo() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (i < 3) {
                sb.append(getRandom()).append("-");
            } else {
                sb.append(getRandom());
            }
        }
        return sb.toString();
    }

    public int getRandom() {
        return (int) ((Math.random() * 9 + 1) * 1000);
    }

    /**
     * 添加paycard信息
     *
     * @param user    操作人
     * @param payCard 充值卡信息
     * @param now     时间
     * @param student 学生
     * @param card    充值卡信息
     */
    /**
     * 添加paycard信息
     *
     * @param user    操作人
     * @param now     时间
     * @param student 学生
     * @param
     */
    private void addPayCard(SysUser user, Date now, Student student, Integer months, Map<String, String> getMap) {
        StudentHours studentHours = new StudentHours();
        studentHours.setCreateTime(now);
        studentHours.setStudentId(student.getId().intValue());
        studentHours.setAdminId(user.getId());
        studentHours.setType(months + "月课");
        studentHoursMapper.insert(studentHours);
        getMap.put("cardNos", "");
    }

    /**
     * 添加学生信息
     *
     * @param student 学生
     * @param time    充课时间
     */
    private void updStudent(Student student, Integer time) {
        int rank = student.getRank() + time;
        student.setRank(rank);
        studentMapper.updateById(student);
        updateRecycleBin(student.getId());
    }


    private void updateRecycleBin(Long studentId) {
        RecycleBin recycleBin = recycleBinMapper.selectByStudentId(studentId);
        if (recycleBin != null) {
            recycleBin.setDelStatus(2);
            recycleBin.setUpdateTime(new Date());
            recycleBinMapper.updateById(recycleBin);
            List<Long> stuIds = new ArrayList<>();
            stuIds.add(studentId);
            rankOpt.recoverFormRecycle(stuIds);
        }
    }

    /**
     * 为学生添加课时
     *
     * @param student
     * @param months
     * @return
     */
    private Integer getStudentTime(Student student, Integer months) {
        int time = 30 * months;
        if (student.getAccountTime() != null) {
            Date accountTime;
            if (System.currentTimeMillis() < student.getAccountTime().getTime()) {
                accountTime = new Date(student.getAccountTime().getTime() + time * 24 * 60 * 60 * 1000L);
            } else {
                accountTime = new Date(System.currentTimeMillis() + time * 24 * 60 * 60 * 1000L);
            }
            student.setAccountTime(accountTime);
        }
        if (student.getRole().equals(5)) {
            student.setRole(1);
        }
        return time;
    }

    /**
     * 判断充课卡数量是否够
     *
     * @param captainCoin 队长币数量
     * @param months      月数
     * @param studentId   学生id
     * @return
     */
    private Map<String, Object> getType(Integer months, Integer captainCoin, Long studentId) {
        Map<String, Object> map = new HashMap<>();
        StudentExpansion expansion = studentExpansionMapper.selectByStudentId(studentId);
        Integer useCaptainCoin = 0;
        map.put("index", 1);
        map.put("useCaptainCoin", useCaptainCoin);
        if (expansion == null || expansion.getPhase() == null || expansion.getPhase().equals("小学")) {
            useCaptainCoin = 400 * months;
            if (captainCoin < useCaptainCoin) {
                map.put("index", 2);
                map.put("message", "队长币数量不足");
                return map;
            }
            return map;
        }
        if (expansion.getPhase().equals("初中")) {
            useCaptainCoin = 400 * months;
            if (captainCoin < useCaptainCoin) {
                map.put("index", 2);
                map.put("message", "队长币数量不足");
                return map;
            }
            return map;
        }
        if (expansion.getPhase().equals("高中")) {
            useCaptainCoin = 500 * months;
            if (captainCoin < useCaptainCoin) {
                map.put("index", 2);
                map.put("message", "队长币数量不足");
                return map;
            }
            return map;
        }
        return map;
    }


    private void getStudentUnit(Student student) {
        if (student.getAccountTime() != null && student.getAccountTime().getTime() < System.currentTimeMillis()) {
            courseService.deleteStudyUnit(student);
        }

    }

}
