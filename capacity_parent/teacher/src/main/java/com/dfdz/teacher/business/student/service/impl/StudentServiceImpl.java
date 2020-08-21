package com.dfdz.teacher.business.student.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfdz.teacher.business.course.service.CourseService;
import com.dfdz.teacher.business.student.service.StudentService;
import com.dfdz.teacher.business.teacher.service.impl.TeacherServiceImpl;
import com.dfdz.teacher.common.CommonMethod;
import com.dfdz.teacher.common.log.factory.LogFactory;
import com.dfdz.teacher.constant.LogNameConst;
import com.dfdz.teacher.feignclient.CenterUserFeignClient;
import com.dfdz.teacher.feignclient.CourseFeignClient;
import com.dfdz.teacher.util.RedisOpt;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.dto.student.AddNewStudentDto;
import com.zhidejiaoyu.common.dto.student.SaveEditStudentInfoDTO;
import com.zhidejiaoyu.common.dto.student.StudentListDto;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejiaoyu.common.rank.RankOpt;
import com.zhidejiaoyu.common.utils.IdUtil;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.manage.EditStudentVo;
import com.zhidejiaoyu.common.vo.student.manage.StudentManageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wuchenxi
 * @date 2020-07-29 15:49:31
 */
@Slf4j
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private CanCreateStudentCountMapper canCreateStudentCountMapper;

    @Resource
    private GradeMapper gradeMapper;

    @Resource
    private CommonMethod commonMethod;

    @Resource
    private CourseNewMapper courseNewMapper;

    @Resource
    private RedisOpt redisOpt;

    @Resource
    private StudentExpansionMapper studentExpansionMapper;

    @Resource
    private CenterUserFeignClient centerUserFeignClient;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private CourseService courseService;

    @Resource
    private SchoolTimeMapper schoolTimeMapper;

    @Resource
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;

    @Resource
    private CourseFeignClient courseFeignClient;

    @Resource
    private RecycleBinMapper recycleBinMapper;

    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    public ServerResponse<PageVo<StudentManageVO>> listStudent(StudentListDto dto) {

        SysUser sysUser = sysUserMapper.selectByOpenId(dto.getOpenId());

        Page<Student> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        List<Student> students = studentMapper.selectStudentManageVO(page, dto, sysUser.getId());

        if (CollectionUtils.isEmpty(students)) {
            PageVo<StudentManageVO> pageVo = PageUtil.packagePage(null, 0L);
            return ServerResponse.createBySuccess(pageVo);
        }

        List<StudentManageVO> collect = students.stream().map(s -> {
            long countDown;
            if (s.getAccountTime() == null) {
                countDown = s.getRank();
            } else {
                countDown = (s.getAccountTime().getTime() - System.currentTimeMillis()) / 86400000;
            }
            StudentManageVO vo = new StudentManageVO();
            vo.setUuid(s.getUuid());
            vo.setAccount(s.getAccount());
            vo.setStudentName(s.getStudentName() == null ? "默认姓名" : s.getStudentName());
            vo.setCountDown(Math.max(0, countDown));
            vo.setCreateTime(DateUtil.formatDate(s.getRegisterDate(), DateUtil.YYYYMMDD));
            return vo;
        }).collect(Collectors.toList());

        PageVo<StudentManageVO> studentManageVOPageVo = PageUtil.packagePage(collect, page.getTotal());
        return ServerResponse.createBySuccess(studentManageVOPageVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object createNewStudent(AddNewStudentDto dto) {
        Integer count = dto.getCount();
        Integer day = dto.getValidity();
        String schoolName = dto.getSchoolName();
        String phase = dto.getPhase();
        if (StringUtils.isEmpty(schoolName)) {
            schoolName = getTeacherExtInfo(dto.getOpenId()).getSchool();
        }
        if (dto.getGrade() == null) {
            dto.setGrade("三年级");
        }
        if (dto.getPhase() == null) {
            dto.setPhase("小学");
        }
        // 检查当前校区还可生成多少个账号
        ServerResponse<Object> tip = this.checkCanCreateCount(count, dto.getOpenId());
        if (!Objects.isNull(tip)) {
            return tip;
        }

        Student student;
        Integer teacherId = null;
        SysUser sysUser = sysUserMapper.selectByOpenId(dto.getOpenId());
        if (sysUser != null) {
            teacherId = sysUser.getId();
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
                businessUserInfo.setNo(dto.getServerNo());
                centerUserFeignClient.getUser(businessUserInfo);
                this.saveOrUpdateStudentExpansion(phase, student);
                this.pushExperienceCourses(student);
            } catch (Exception e) {
                log.error("批量生成学生信息失败!", e);
                throw new ServiceException(500, "服务器异常");
            }
        }
        //super.saveLog(LogNameConst.CREATE_ACCOUNT, sb.toString());
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<EditStudentVo> getEditStudentVoByUuid(String uuid) {

        Student student = studentMapper.selectByUuid(uuid);
        Grade grade = null;
        if (student.getClassId() != null) {
            grade = gradeMapper.selectById(student.getClassId());
        }
        EditStudentVo vo = new EditStudentVo();
        vo.setUuid(student.getUuid());
        vo.setAccount(student.getAccount());
        vo.setArea(student.getArea());
        vo.setBirthDay(student.getBirthDate());
        vo.setCity(student.getCity());
        vo.setClassName(grade == null ? "未分班" : grade.getClassName());
        vo.setGrade(student.getGrade());
        vo.setMail(student.getMail());
        vo.setNickName(student.getNickname());
        vo.setPassword(student.getPassword());
        vo.setPhone(student.getPatriarchPhone());
        vo.setProvince(student.getProvince());
        vo.setQq(student.getQq());
        vo.setRank(student.getRank());
        vo.setSchoolName(student.getSchoolName());
        vo.setSex(student.getSex());
        vo.setStudentName(student.getStudentName()==null||student.getStudentName().equals("")?"默认姓名":student.getStudentName());
        vo.setWish(student.getWish());
        vo.setVersion(getStudentVersion(student));
        return ServerResponse.createBySuccess(vo);
    }

    private String getStudentVersion(Student student) {
        StudentStudyPlanNew studentStudyPlanNew = studentStudyPlanNewMapper.selectMaxFinalByStudentId(student.getId());
        if (studentStudyPlanNew != null) {
            CourseNew course = courseFeignClient.getById(studentStudyPlanNew.getCourseId());
            return course.getVersion();
        } else {
            Integer schoolAdminId = teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId());
            SchoolTime schoolTime = schoolTimeMapper.selectByUserIdAndGrade(schoolAdminId, student.getGrade() == null ? "三年级" : student.getGrade());
            if (schoolTime != null) {
                CourseNew course = courseFeignClient.getById(schoolTime.getCourseId());
                return course.getVersion();
            } else {
                SchoolTime schoolTime1 = schoolTimeMapper.selectByUserIdAndGrade(1, student.getGrade() == null ? "三年级" : student.getGrade());
                if (schoolTime1 == null) {
                    return "未找到版本";
                }
                CourseNew course = courseFeignClient.getById(schoolTime1.getCourseId());
                return course.getVersion();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse<Object> saveStudentInfoAfterEdit(SaveEditStudentInfoDTO dto) {

        Student student = new Student();
        BeanUtils.copyProperties(dto, student);

        Student oldStudent = studentMapper.selectByUuid(dto.getUuid());
        student.setUpdateTime(new Date());
        if(student.getBirthDate().equals("")){
            student.setBirthDate(null);
        }
        student.setId(oldStudent.getId());
        int count = studentMapper.updateById(student);

        if (count == 0) {
            return ServerResponse.createByError();
        }

        // 判断学生年级是否修改
        if (!oldStudent.getGrade().equals(student.getGrade()) || (StringUtil.isNotEmpty(oldStudent.getVersion()) && !oldStudent.getVersion().equals(student.getVersion()))) {
            Integer integer = testRecordMapper.countByGenreAndStudentId(GenreConstant.TEST_BEFORE_STUDY, student.getId());
            if (oldStudent.getRank() > 7 && integer > 0) {
                courseService.deleteStudyUnit(student);
            }
        }
        String phase = this.getPhase(student.getGrade());

        this.saveOrUpdateStudentExpansion(phase, student);


        SysUser sysUser = sysUserMapper.selectByOpenId(dto.getOpenId());

        LogFactory.saveLog(sysUser.getId(), LogNameConst.UPDATE_STUDENT, sysUser.getAccount() + " -> " + sysUser.getName()
                + " 修改学生信息：原信息[" + oldStudent.toString() + "]，修改后信息[" + student.toString() + "]；");

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Object> deleteStudentByUuid(String studentUuid, String userUuid) {
        SysUser user = sysUserMapper.selectByUuid(userUuid);
        Student student = studentMapper.selectByUuid(studentUuid);
        RecycleBin recycleBin = new RecycleBin();
        recycleBin.setCreateTime(new Date());
        recycleBin.setOperateUserId((long) user.getId());
        recycleBin.setOperateUserName(user.getName());
        recycleBin.setStudentId(student.getId());
        try {
            studentMapper.deleteByStudentId(student.getId());
            recycleBinMapper.insert(recycleBin);

            this.saveDeleteStudentLog(new Long[]{student.getId()}, user);

            redisOpt.deleteCaches(Collections.singletonList(student.getId()));
        } catch (Exception e) {
            log.error("{} -> {} 删除学生信息出错！", user.getAccount(), user.getName(), e);
            throw new RuntimeException("删除学生信息失败！", e);
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 保存删除学生信息日志
     *
     * @param ids
     */
    private void saveDeleteStudentLog(Long[] ids, SysUser user) {
        List<Student> students = studentMapper.selectByIds(Arrays.asList(ids));

        StringBuilder sb = new StringBuilder();
        sb.append(user.getAccount()).append(" -> ").append(user.getName()).append(" 删除了学生：");
        students.forEach(student -> sb.append(student.getAccount()).append(" -> ").append(student.getStudentName()).append("，"));
        OperationLog operationLog = new OperationLog();
        operationLog.setLogname("删除学生");
        operationLog.setUserid(user.getId());
        operationLog.setCreatetime(new Date());
        operationLog.setSucceed("成功");
        operationLog.setMessage(sb.toString());
        operationLog.setMessage("业务日志");
        operationLogMapper.insert(operationLog);
    }

    private String getPhase(String grade) {
        switch (grade) {
            case "七年级":
            case "八年级":
            case "九年级":
                return "初中";
            case "高一":
            case "高二":
            case "高三":
                return "高中";
            default:
                return "小学";
        }
    }

    /**
     * 推送体验版课程
     *
     * @param student
     */
    private void pushExperienceCourses(Student student) {
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

    private Teacher getTeacherExtInfo(String openId) {
        SysUser sysUser = sysUserMapper.selectByOpenId(openId);
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

    private ServerResponse<Object> checkCanCreateCount(Integer count, String openId) {
        CreateStudentServiceImpl.CreateCount canCreateCount = CreateStudentServiceImpl.getCanCreateCount(canCreateStudentCountMapper, studentMapper, sysUserMapper, openId);
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
