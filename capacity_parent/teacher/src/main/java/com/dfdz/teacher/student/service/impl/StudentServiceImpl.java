package com.dfdz.teacher.student.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.teacher.business.teacher.service.impl.TeacherServiceImpl;
import com.dfdz.teacher.common.CommonMethod;
import com.dfdz.teacher.common.Const;
import com.dfdz.teacher.common.exception.BizExceptionEnum;
import com.dfdz.teacher.dto.AddNewStudentDto;
import com.dfdz.teacher.exception.GunsException;
import com.dfdz.teacher.feignclient.CenterUserFeignClient;
import com.dfdz.teacher.student.service.StudentService;
import com.dfdz.teacher.util.RedisOpt;
import com.zhidejiaoyu.common.constant.ServerNoConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejiaoyu.common.utils.IdUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private CanCreateStudentCountMapper canCreateStudentCountMapper;
    @Resource
    private StudentMapper studentMapper;
    @Autowired
    private CommonMethod commonMethod;
    @Autowired
    private RedisOpt redisOpt;
    @Resource
    private StudentExpansionMapper studentExpansionMapper;
    @Resource
    private CourseNewMapper courseNewMapper;
    @Resource
    private CenterUserFeignClient centerUserFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object createNewStudent(AddNewStudentDto dto) {
        Integer count = dto.getCount();
        Integer day = dto.getValidity();
        String schoolName = dto.getSchoolName();
        String phase = dto.getPhase();
        if (StringUtils.isEmpty(schoolName)) {
            schoolName = getTeacherExtInfo(dto.getAdminUUID()).getSchool();
        }
        if (dto.getGrade() == null) {
            dto.setGrade("三年级");
        }
        if (dto.getPhase() == null) {
            dto.setPhase("小学");
        }
        // 检查当前校区还可生成多少个账号
        ServerResponse tip = this.checkCanCreateCount(count, dto.getAdminUUID());
        if (!Objects.isNull(tip)) {
            return tip;
        }

        Student student;
        Integer teacherId = null;
        if (getCurrentUserId(dto.getAdminUUID()) != null) {
            teacherId = getCurrentUserId(dto.getAdminUUID());
        }

        StringBuilder sb = new StringBuilder("生成账号数量：").append(count).append("；生成账号名称：");

        RedisOpt.AccountRange accountRange = redisOpt.getAccountRange(count);
        for (long accountNum = accountRange.getCurrent() + 1; accountNum <= accountRange.getMax(); accountNum++) {
            student = this.packageNewStudent(day, schoolName, teacherId, accountNum);
            student.setRole(1);
            student.setVersion(dto.getVersion());
            student.setGrade(dto.getGrade());
            student.setUuid(IdUtil.getId());
            sb.append(student.getAccount()).append("  ").append(day).append("天#");
            try {
                studentMapper.insert(student);
                BusinessUserInfo businessUserInfo = new BusinessUserInfo();
                businessUserInfo.setAccount(student.getAccount());
                businessUserInfo.setPassword(student.getPassword());
                businessUserInfo.setUserUuid(student.getUuid());
                centerUserFeignClient.getUser(businessUserInfo, ServerNoConstant.SERVER_NO);
                this.saveOrUpdateStudentExpansion(phase, student);
                this.pushExperienceCourses(student, phase);
            } catch (Exception e) {
                log.error("批量生成学生信息失败!", e);
                throw new GunsException(500, "服务器异常");
            }
        }
        //super.saveLog(LogNameConst.CREATE_ACCOUNT, sb.toString());

        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "成功");
        map.put("url", "student/account/student/createStudent");
        return map;
    }

    /**
     * 推送体验版课程
     *
     * @param student
     * @param phase
     */
    private void pushExperienceCourses(Student student, String phase) {
        // 获取所有体验版课程
        List<CourseNew> experienceCourses = courseNewMapper.selectExperienceCourses();
        // 推送体验版课程
        commonMethod.initUnit(student, experienceCourses, null, null);

       /* // 推送体验版初始的智能版课程
        this.pushEssenceCourse(student, phase, 1);*/
    }

    /**
     * 保存或者更新学生扩展信息
     *
     * @param phase
     * @param student
     */
    private void saveOrUpdateStudentExpansion(String phase, Student student) {
        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(student.getId());
        if (studentExpansion == null) {
            studentExpansion = new StudentExpansion();
            studentExpansion.setStudentId(student.getId());
            studentExpansion.setPhase(phase);
            studentExpansion.setSourcePower(0);
            studentExpansionMapper.insert(studentExpansion);
        } else {
            studentExpansion.setPhase(phase);
            studentExpansionMapper.updateById(studentExpansion);
        }
    }

    private Integer getCurrentUserId(String adminUUID) {
        return sysUserMapper.selectByUuid(adminUUID).getId();
    }

    private Teacher getTeacherExtInfo(String adminUUID) {
        SysUser sysUser = sysUserMapper.selectByUuid(adminUUID);
        return teacherMapper.selectTeacherBySchoolAdminId(sysUser.getId());
    }

    /**
     * 封装学生信息
     *
     * @param day
     * @param schoolName
     * @param teacherId
     * @param accountNum 账号中数字数据
     * @return
     */
    private Student packageNewStudent(Integer day, String schoolName, Integer teacherId, long accountNum) {
        Teacher teacher = null;
        if (teacherId != null) {
            teacher = teacherMapper.selectByTeacherId(teacherId.longValue());
            if (teacher == null) {
                teacher = teacherMapper.selectSchoolAdminById(teacherId);
            }
        }

        Student student = new Student();
        student.setUuid(IdUtil.getId());
        Map<String, String> accountAndPassword = this.getAccountAndPassword(accountNum);
        student.setAccount(accountAndPassword.get("account"));
        student.setPassword(accountAndPassword.get("password"));

        // 生成有效期
        student.setNickname("默认昵称");
        student.setRank(day);
        student.setSchoolName(schoolName);
        student.setTeacherId(teacherId.longValue());
        if (teacher == null) {
            // 管理员生成的学生
            student.setProvince("北京市");
            student.setCity("市辖区");
            student.setArea("海淀区");
        } else {
            student.setProvince(teacher.getProvince());
            student.setCity(teacher.getCity());
            student.setArea(teacher.getArea());
        }
        student.setRegisterDate(new Date());
        student.setEnergy(0);
        student.setDiamond(0);
        return student;
    }

    private ServerResponse checkCanCreateCount(Integer count, String adminUUID) {
        CreateStudentServiceImpl.CreateCount canCreateCount = CreateStudentServiceImpl.getCanCreateCount(canCreateStudentCountMapper, studentMapper, sysUserMapper, adminUUID);
        if (count > canCreateCount.getCanCreateCount()) {
            return ServerResponse.createByError(400, "最多还可生成 " + canCreateCount.getCanCreateCount() + " 个账号！");
        }
        return null;
    }

    /**
     * 生成学生账号和密码
     *
     * @param accountNum 账号中数字数据
     * @return 学生账号和密码 key：account password
     */
    private Map<String, String> getAccountAndPassword(long accountNum) {

        Map<String, String> map = new HashMap<>(16);
        String accountNumStr = String.valueOf(accountNum);
        String password = String.valueOf(new Random().nextInt(900000) + 100000);
        map.put("password", password);
        map.put("account", "dz" + TeacherServiceImpl.finalRandom(accountNumStr, accountNumStr.length()));
        return map;
    }

}
