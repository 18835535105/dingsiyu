package com.zhidejiaoyu.common.pojo.center;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * business_user_info
 * @author
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BusinessUserInfo extends Model<BusinessUserInfo> implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * server_config关联表
     */
    private String serverConfigId;

    /**
     * 学生或者学管的uuid
     */
    private String userUuid;

    /**
     * 学生或者学管的账号
     */
    private String account;

    /**
     * 学生或者学管的密码
     */
    private String password;

    /**
     * 微信openid
     */
    private String openid;

    private Date createTime;
    private Date updateTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    private static final long serialVersionUID = 1L;
}
