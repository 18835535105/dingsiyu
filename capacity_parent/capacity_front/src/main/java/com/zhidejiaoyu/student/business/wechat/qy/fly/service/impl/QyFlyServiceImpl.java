package com.zhidejiaoyu.student.business.wechat.qy.fly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.common.dto.student.StudentStudyPlanListDto;
import com.zhidejiaoyu.common.dto.wechat.qy.fly.SearchStudentDTO;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.feignclient.course.CourseCourseFeginClient;
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

    @Resource
    private CourseCourseFeginClient courseCourseFeginClient;

    @Resource
    private UnitFeignClient unitFeignClient;

    @Override
    public ServerResponse<Map<String, Object>> getStudentStudyPlan(StudentStudyPlanListDto dto) {
        Student student = studentMapper.selectByUuid(dto.getUuid());
        PageHelper.startPage(PageUtil.getPageNum(), PageUtil.getPageSize());
        List<StudentStudyPlanNew> studentStudyPlanNews = studentStudyPlanNewMapper.selectStudyPlanByStudentIdAndPage(student.getId());
        PageInfo<StudentStudyPlanNew> pageInfo = new PageInfo<>(studentStudyPlanNews);
        Map<String, Object> map = new HashMap<>();
        map.put("studentName", student.getStudentName());
        map.put("studentAccount", student.getAccount());
        List<StudentStudyPlanListVo> list = new ArrayList<>();
        if (studentStudyPlanNews.size() > 0) {
            List<Long> unitIds = new ArrayList<>();
            studentStudyPlanNews.forEach(plan -> {
                unitIds.add(plan.getUnitId());
            });
            CourseNew course = courseCourseFeginClient.getById(studentStudyPlanNews.get(0).getCourseId());
            Map<Long, Map<String, Object>> longMapMap = unitFeignClient.selectUnitNameByUnitIds(unitIds);
            map.put("courseName", course.getVersion());
            studentStudyPlanNews.forEach(plan -> {
                list.add(StudentStudyPlanListVo.builder()
                        .unitName(longMapMap.get(plan.getUnitId()).get("unitName").toString())
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

        PageHelper.startPage(PageUtil.getPageNum(), PageUtil.getPageSize());
        List<Student> students = studentMapper.selectByTeacherIdOrSchoolAdminId(sysUser.getId(), dto);
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
