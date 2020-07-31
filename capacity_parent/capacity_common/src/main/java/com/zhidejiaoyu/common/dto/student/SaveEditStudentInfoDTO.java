package com.zhidejiaoyu.common.dto.student;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * 教师后台修改学生信息保存数据dto
 *
 * @author wuchenxi
 * @date 2020-07-30 14:42:59
 */
@Data
public class SaveEditStudentInfoDTO {

    /**
     * 教师openId
     */
    @NotNull(message = "openId can't be null!")
    private String openId;

    /**
     * 学生uuid
     */
    @NotNull(message = "uuid can't be null!")
    private String uuid;

    /**
     * 班级id
     */
    private Long classId;

    @Length(min = 6, max = 10, message = "密码长度必须在6~10之间！")
    private String password;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String area;

    /**
     * 学生名
     */
    @Length(min = 2, max = 20, message = "学生姓名字符长度需在2~20之间！")
    private String studentName;

    /**
     * 性别 1：男 2：女
     */
    @Range(min = 1, max = 2, message = "sex 参数非法！")
    private Integer sex;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 出生日期
     */
    private String birthDate;

    /**
     * 年级
     */
    private String grade;

    /**
     * 班级
     */
    private String squad;

    /**
     * 愿望
     */
    private String wish;

    /**
     * 家长手机号
     */
    @Length(min = 11, max = 11, message = "手机号码输入错误！")
    private String patriarchPhone;

    @Email
    private String mail;

    /**
     * qq
     */
    private String qq;

    /**
     * 教材版本
     */
    private String version;

}
