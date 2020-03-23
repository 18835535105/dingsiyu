package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 教师相关信息
 * </p>
 *
 * @author zdjy
 * @since 2018-12-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Teacher extends Model<Teacher> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 教师id
     */
    private Integer teacherId;
    /**
     * 学校管理员id
     */
    private Integer schoolAdminId;
    /**
     * 学校名称
     */
    private String school;
    /**
     * 教师密码明文
     */
    private String password;
    /**
     * 省份

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
     * 昵称
     */
    private String nickname;
    /**
     * QQ
     */
    private String qq;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
