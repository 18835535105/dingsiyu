package com.zhidejiaoyu.common.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保存学生信息到中台服务dto
 *
 * @author: wuchenxi
 * @date: 2020/7/6 10:47:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveStudentInfoToCenterDTO {

    /**
     * 学生所在服务器编号
     */
    private String serverNo;

    /**
     * 学生账号
     */
    private String account;
    private String openid;
    private String password;

    /**
     * 学生uuid
     */
    private String uuid;
}
