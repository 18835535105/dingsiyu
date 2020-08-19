package com.zhidejiaoyu.student.business.wechat.qy.fly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.dto.student.StudentStudyPlanListDto;
import com.zhidejiaoyu.common.dto.testbeforestudy.GradeAndUnitIdDTO;
import com.zhidejiaoyu.common.dto.wechat.qy.fly.SearchStudentDTO;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.feignclient.course.CourseFeignClient;
import com.zhidejiaoyu.student.business.feignclient.course.UnitFeignClient;
import com.zhidejiaoyu.student.business.wechat.qy.fly.service.QyFlyService;
import com.zhidejiaoyu.student.business.wechat.qy.fly.vo.SearchStudentVO;
import com.zhidejiaoyu.student.business.wechat.qy.fly.vo.StudentStudyPlanListVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wuchenxi
 * @date: 2020/6/16 13:54:54
 */
@Service
public class QyFlyServiceImpl extends ServiceImpl<CurrentDayOfStudyMapper, CurrentDayOfStudy> implements QyFlyService {

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private CurrentDayOfStudyMapper currentDayOfStudyMapper;

    @Resource
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;

    private final CourseFeignClient courseFeignClient;

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private GradeMapper gradeMapper;

    @Resource
    private UnitFeignClient unitFeignClient;

    public QyFlyServiceImpl(CourseFeignClient courseFeignClient) {
        this.courseFeignClient = courseFeignClient;
    }

    @Override
    public ServerResponse<Map<String, Object>> getStudentStudyPlan(StudentStudyPlanListDto dto) {
        Student student = studentMapper.selectByUuid(dto.getUuid());
        if(dto.getPageNum()==null){
            dto.setPageNum(1);
        }
        if(dto.getPageSize()==null){
            dto.setPageSize(20);
        }
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<StudentStudyPlanNew> studentStudyPlanNews = studentStudyPlanNewMapper.selectStudyPlanByStudentIdAndPage(student.getId());
        PageInfo<StudentStudyPlanNew> pageInfo = new PageInfo<>(studentStudyPlanNews);
        Map<String, Object> map = new HashMap<>();
        map.put("studentName", student.getStudentName());
        map.put("studentAccount", student.getAccount());
        List<StudentStudyPlanListVo> list = new ArrayList<>();
        if (studentStudyPlanNews.size() > 0) {
            List<Long> unitIds = new ArrayList<>();
            List<Long> courseIds=new ArrayList<>();
            studentStudyPlanNews.forEach(plan -> unitIds.add(plan.getUnitId()));
            studentStudyPlanNews.forEach(plan -> courseIds.add(plan.getCourseId()));
            CourseNew course = courseFeignClient.getById(studentStudyPlanNews.get(0).getCourseId());
            Map<Long, Map<String, Object>> longMapMap = unitFeignClient.selectUnitNameByUnitIds(unitIds);
            Map<Long, Map<String, Object>> courseMap = courseFeignClient.selectGradeAndLabelByCourseIds(courseIds);
            map.put("courseName", course.getVersion());
            studentStudyPlanNews.forEach(plan -> {
                Map<String, Object> couMap = courseMap.get(plan.getCourseId());
                list.add(StudentStudyPlanListVo.builder()
                        .unitName(couMap.get("grade")+"-"+couMap.get("label")+"-"+longMapMap.get(plan.getUnitId()).get("name").toString())
                        .model(plan.getEasyOrHard() == 1 ? "正常" : "进阶")
                        .finalLevel(plan.getFinalLevel().toString())
                        .build());
            });
            PageVo<StudentStudyPlanListVo> page = PageUtil.packagePage(list, pageInfo.getTotal());
            map.put("list", page);
        }
        return ServerResponse.createBySuccess(map);
    }


    @Override
    public ServerResponse<Object> getStudents(SearchStudentDTO dto) {

        SysUser sysUser = sysUserMapper.selectByOpenId(dto.getOpenId());
        Grade grade = gradeMapper.selectByTeacherId(sysUser.getId().longValue());

        List<Student> students;
        if (sysUser.getAccount().contains("xg") || grade != null) {
            PageHelper.startPage(PageUtil.getPageNum(), PageUtil.getPageSize());
            students = studentMapper.selectByTeacherIdOrSchoolAdminId(sysUser.getId(), dto);
        } else {
            Integer teacherId = teacherMapper.selectSchoolAdminIdByTeacherId(sysUser.getId().longValue());
            PageHelper.startPage(PageUtil.getPageNum(), PageUtil.getPageSize());
            students = studentMapper.selectByTeacherIdOrSchoolAdminId(teacherId, dto);
        }

        PageInfo<Student> pageInfo = new PageInfo<>(students);

        // 查询学生当天是否已经上传了飞行记录
        Map<Long, Map<Long, Long>> map = new HashMap<>(16);
        if (CollectionUtils.isNotEmpty(students)) {
            map = currentDayOfStudyMapper.countByStudentIdsAndDate(students.stream().map(Student::getId).collect(Collectors.toList()), new Date());
        }

        Map<Long, Map<Long, Long>> finalMap = map;
        List<SearchStudentVO> collect = students.stream().map(student -> SearchStudentVO.builder()
                .studentId(student.getId())
                .uuid(student.getUuid())
                .studentName(StringUtil.isEmpty(student.getStudentName()) ? "默认姓名" : student.getStudentName())
                .account(student.getAccount())
                .canSubmit(finalMap.get(student.getId()) == null || finalMap.get(student.getId()).get("count") == 0)
                .build()).collect(Collectors.toList());

        PageVo<SearchStudentVO> page = PageUtil.packagePage(collect, pageInfo.getTotal());
        return ServerResponse.createBySuccess(page);
    }

    @Override
    public boolean checkUpload(String uuid) {
        Student student = studentMapper.selectByUuid(uuid);
        int count = currentDayOfStudyMapper.countByStudentIdAndDate(student.getId(), new Date());
        return count == 0;
    }

    @Override
    public List<String> getFlyCalendar(String uuid, String month) {

        Student student = studentMapper.selectByUuid(uuid);

        return currentDayOfStudyMapper.selectCreateTimeByMonth(student.getId(), month);
    }

    @Override
    public ServerResponse<Object> checkScanQrCode(Long studentId, Integer num) {
        int i = currentDayOfStudyMapper.countByStudentIdAndQrCodeNum(studentId, num);
        if (i > 0) {
            return ServerResponse.createByError(400, "已经上传，再上传将覆盖原有信息！是否继续上传？");
        }
        return ServerResponse.createBySuccess();
    }
}
