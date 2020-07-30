package com.zhidejiaoyu.common.vo.student.manage;

import lombok.Data;

import java.io.Serializable;

/**
 * 后台学生列表
 *
 * @author wuchenxi
 * @date 2018年4月25日 下午5:03:46
 */
@Data
public class StudentManageVO implements Serializable {

    private String uuid;

    private String account;

    private String studentName;

    /**
     * 倒计时，剩余天数
     */
    private Long countDown;

    /**
     * 有效期
     */
    private String createTime;

}

