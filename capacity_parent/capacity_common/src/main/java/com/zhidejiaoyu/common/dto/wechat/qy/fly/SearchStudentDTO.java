package com.zhidejiaoyu.common.dto.wechat.qy.fly;

import lombok.Data;

/**
 * 学生查询条件
 *
 * @author: wuchenxi
 * @date: 2020/6/16 15:04:04
 */
@Data
public class SearchStudentDTO {

    private String teacherUuid;

    /**
     * 根据账号或者姓名查询
     */
    private String accountOrName;
}
