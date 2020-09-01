package com.dfdz.teacher.feignclient;

import com.zhidejiaoyu.common.dto.student.SaveStudentInfoToCenterDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 12:30:30
 */
@FeignClient(name = "center", path = "/center")
public interface CenterUserFeignClient {

    /**
     * 向中台服务器批量保存用户信息
     *
     * @param saveUserInfoDTOList
     * @return
     */
    @PostMapping("/userInfo/saveUserInfos")
    boolean saveUserInfos(@RequestBody List<SaveStudentInfoToCenterDTO> saveUserInfoDTOList);
}
