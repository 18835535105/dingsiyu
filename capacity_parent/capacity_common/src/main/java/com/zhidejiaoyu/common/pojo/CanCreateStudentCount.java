package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 校区可生成学生临时账号个数
 *
 * @author: wuchenxi
 * @date: 2020/7/16 10:37:37
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CanCreateStudentCount extends Model<CanCreateStudentCount> {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private Integer schoolAdminId;

    private Integer canCreateCount;

    private Date createTime;

    private Date updateTime;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
