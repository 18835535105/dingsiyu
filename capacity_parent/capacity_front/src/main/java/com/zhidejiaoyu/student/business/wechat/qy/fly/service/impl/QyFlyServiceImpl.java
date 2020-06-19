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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
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

    @Override
    public ServerResponse<Object> uploadFlyRecord(MultipartFile file, Long studentId, Integer num) {

        String dir = FileConstant.STUDENT_FLY_RECORD + DateUtil.formatYYYYMMDD(new Date()) + "/";

        executorService.execute(() -> {
            String upload = OssUpload.upload(file, dir, IdUtil.getId());

            this.save(CurrentDayOfStudy.builder()
                    .createTime(new Date())
                    .imgUrl(upload)
                    .qrCodeNum(num)
                    .studentId(studentId)
                    .build());
        });

        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<Object> getStudents(String openId, SearchStudentDTO dto) {

        SysUser sysUser = sysUserMapper.selectByOpenId(openId);

        PageHelper.startPage(PageUtil.getPageNum(), PageUtil.getPageSize());
        List<Student> students = studentMapper.selectByTeacherIdOrSchoolAdminId(sysUser.getId(), dto);
        PageInfo<Student> pageInfo = new PageInfo<>(students);

        List<SearchStudentVO> collect = students.stream().map(student -> SearchStudentVO.builder()
                .studentId(student.getId())
                .studentName(student.getStudentName())
                .build()).collect(Collectors.toList());

        PageVo<SearchStudentVO> page = PageUtil.packagePage(collect, pageInfo.getTotal());
        return ServerResponse.createBySuccess(page);
    }
}
