package com.zhidejioayu.center.business.feignclient.teacher;

import com.zhidejiaoyu.common.dto.student.StudentListDto;
import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.page.PageVo;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.manage.StudentManageVO;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 教师服务
 *
 * @author wuchenxi
 * @date 2020-07-27 11:50:12
 */
public interface BaseTeacherInfoFeignClient {

    @GetMapping("/teacher/getByUuid")
    ServerResponse<SysUser> getByUuid(@RequestParam String uuid);

    /**
     * 查询学生列表
     *
     * @param dto
     * @return
     */
    @GetMapping("/student/listStudent")
    ServerResponse<PageVo<StudentManageVO>> listStudent(@SpringQueryMap StudentListDto dto);
}
