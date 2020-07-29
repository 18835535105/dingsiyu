package com.dfdz.teacher.business.teacher.controller;

import com.dfdz.teacher.business.teacher.service.TeacherService;
import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wuchenxi
 * @date 2020-07-29 10:41:07
 */
@RestController
@RequestMapping
public class TeacherController {

    @Resource
    private TeacherService teacherService;

    /**
     * 根据uuid获取学管信息
     *
     * @param uuid
     * @return
     */
    @GetMapping("/getByUuid")
    public ServerResponse<SysUser> getByUuid(@RequestParam String uuid) {
        if (StringUtil.isEmpty(uuid)) {
            return ServerResponse.createByError(400, "uuid can't be null!");
        }
        return teacherService.getByUuid(uuid);
    }
}
