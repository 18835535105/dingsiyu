package com.zhidejiaoyu.student.business.wechat.qy.fly.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhidejiaoyu.aliyunoss.putObject.OssUpload;
import com.zhidejiaoyu.common.constant.FileConstant;
import com.zhidejiaoyu.common.dto.wechat.qy.fly.SearchStudentDTO;
import com.zhidejiaoyu.common.mapper.CurrentDayOfStudyMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.SysUserMapper;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.IdUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.qy.fly.service.QyFlyService;
import com.zhidejiaoyu.student.business.wechat.qy.fly.vo.SearchStudentVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
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
    private ExecutorService executorService;

    @Resource
    private CurrentDayOfStudyMapper currentDayOfStudyMapper;

    @Override
    public ServerResponse<Object> uploadFlyRecord(MultipartFile file, Long studentId, Integer num) {

        Date date = new Date();
        int count = currentDayOfStudyMapper.countByStudentIdAndDate(studentId, date);
        if (count > 0) {
            return ServerResponse.createByError(400, "学生当天信息已经上传，不能再次上传！");
        }

        String dir = FileConstant.STUDENT_FLY_RECORD + DateUtil.formatYYYYMMDD(date) + "/";

        executorService.execute(() -> {
            String upload = OssUpload.upload(file, dir, IdUtil.getId());

            this.save(CurrentDayOfStudy.builder()
                    .createTime(date)
                    .imgUrl(upload)
                    .qrCodeNum(num)
                    .studentId(studentId)
                    .build());
        });

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Object> getStudents(SearchStudentDTO dto) {

        SysUser sysUser = sysUserMapper.selectByUuid(dto.getTeacherUuid());

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
                .studentName(student.getStudentName())
                .canSubmit(finalMap.get(student.getId()) == null || finalMap.get(student.getId()).get("count") == 0)
                .build()).collect(Collectors.toList());

        PageVo<SearchStudentVO> page = PageUtil.packagePage(collect, pageInfo.getTotal());
        return ServerResponse.createBySuccess(page);
    }

    @Override
    public boolean checkUpload(String studentUuid) {
        Student student = studentMapper.selectByUuid(studentUuid);
        int count = currentDayOfStudyMapper.countByStudentIdAndDate(student.getId(), new Date());
        return count == 0;
    }
}
