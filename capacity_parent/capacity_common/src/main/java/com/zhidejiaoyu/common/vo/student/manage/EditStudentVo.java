package com.zhidejiaoyu.common.vo.student.manage;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改学生信息数据 vo
 *
 * @author wuchenxi
 * @date 2019-04-09
 */
@Data
public class EditStudentVo implements Serializable {

    /**
     * 学生 uuid
     */
    private String uuid;

    private String account;

    private String password;

    private Integer rank;

    private String studentName;

    private Integer sex;

    private String schoolName;

    private String province;

    private String city;

    private String area;

    private String nickName;

    private String birthDay;

    /**
     * 年级
     */
    private String grade;

    private String className;

    private String wish;

    private String phone;

    private String qq;

    private String mail;

    private String version;

}
