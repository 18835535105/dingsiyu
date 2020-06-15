package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 学生信息表
 *
 * @author wuchenxi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Student extends Model<Student> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String uuid;

    /**
     * 老师id
     */
    private Long teacherId;
    /**
     * 班级id
     */
    private Long classId;

    /**
     * 账号
     */
    private String account;

    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 单元id
     */
    private Long unitId;

    /**
     * 课程名
     */
    private String courseName;
    /**
     * 单元名
     */
    private String unitName;

    @Size(min = 6, max = 10, message = "密码长度必须在6~10之间！")
    private String password;

    /**
     * 有效期
     */
    @Future
    private Date accountTime;

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
     * 学校名
     */
    private String schoolName;

    /**
     * 学生名
     */
    @Min(value = 2, message = "学生姓名不能少于2个字符！")
    @Max(value = 20, message = "学生姓名不能超过20个字符！")
    private String studentName;

    /**
     * 性别 1：男 2：女
     */
    private Integer sex;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 出生日期
     */
    @Past
    private String birthDate;

    /**
     * 账号注册日期
     */
    private Date registerDate;

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
     * 手机号,未用到
     */
    @Deprecated
    private String phone;

    /**
     * 家长手机号
     */
    private String patriarchPhone;

    @Email
    private String mail;

    /**
     * 账号状态 1：开启 2：关闭 3:删除
     */
    private Integer status;

    /**
     * 头像地址 可作为学生是否完成信息完善标识，为空说明未完善信息，否则为完善过信息
     */
    private String headUrl;

    /**
     * 头像名
     */
    private String headName;

    /**
     * 地址
     */
    private String address;

    /**
     * qq
     */
    private String qq;

    /**
     * 实际学校名
     */
    private String practicalSchool;

    /**
     * 推荐人
     */
    private String referrer;

    /**
     * 存入的时候精确到小数点后两位 取出的时候四舍五入取整数 线下金币（已消费金币总和）
     */
    private Double offlineGold;

    /**
     * 存入的时候精确到小数点后两位 取出的时候四舍五入取整数 系统金币（当前系统内剩余金币）
     */
    private Double systemGold;

    /**
     * 学生金币全校排行第一名时的时间，用于计算学生到达第一名与失去第一名的时间差
     */
    private Date schoolGoldFirstTime;

    /**
     * 学生金币全国排行第一名的时间，用于计算学生达到第一名与失去第一名的时间差
     */
    private Date countryGoldFirstTime;

    /**
     * 学生被膜拜总次数达到全国第一时，记录时间，用于计算学生达到第一名与失去第一名的时间差
     */
    private Date worshipFirstTime;
    /**
     * 有效期 7天，14天，30天，365天
     */
    @TableField("`rank`")
    private Integer rank;

    private Date updateTime;

    /**
     * 角色，1：清学版用户 ; 2：智能版学生用户；3：业务用户；4：福利账号（招生账号）
     */
    private Integer role;

    /**
     * 第一次学习的时间
     */
    private Date firstStudyTime;

    /**
     * 角色图片url(宠物图片地址)
     */
    private String partUrl;

    /**
     * 宠物名称
     */
    private String petName;

    /**
     * 当前例句所学的课程id
     */
    private Long sentenceCourseId;

    /**
     * 当前例句所学的课程id
     */
    private Integer sentenceUnitId;

    /**
     * 当前学习例句的课程名(拼接名)
     */
    private String sentenceCourseName;

    /**
     * 当前学习例句的单元名
     */
    private String sentenceUnitName;

    /**
     * 进入慧追踪时是否显示提示框。
     * 1：显示提示框；
     * 2：不显示提示框
     */
    private Integer showCapacity;

    /**
     * 教材版本
     */
    private String version;

    /**
     * 能量数字   每天凌晨12点能量清零
     */
    private Integer energy;

    /**
     * 钻石数量
     */
    private Integer diamond;

    /**
     * 小程序openid，用于绑定队长账号，多个小程序之间用 , 隔开
     */
    private String openid;

    /**
     * 金币加成到期时间
     */
    private Date bonusExpires;

    public Student(String petName) {
        this.petName = petName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.patriarchPhone = phone;
    }

    public void setPatriarchPhone(String patriarchPhone) {
        this.phone = patriarchPhone;
        this.patriarchPhone = patriarchPhone;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
