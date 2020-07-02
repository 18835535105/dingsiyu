package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2018-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("join_school")
public class JoinSchool extends Model<JoinSchool> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;
    /**
     * 学校名称
     */
    @TableField("school_name")
    private String schoolName;
    /**
     * 加盟日期
     */
    @TableField("date_of_joining")
    private Date dateOfJoining;
    /**
     * 校长姓名
     */
    @TableField("pessident_name")
    private String pessidentName;
    /**
     * 电话
     */
    private String phone;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 加盟校数量
     */
    @TableField("joining_number")
    private Integer joiningNumber;
    /**
     * 加盟校地址
     */
    private String address;
    /**
     * 加盟校状态
     */
    @TableField("audit_status")
    private Integer auditStatus;
    /**
     * 预约校单号
     */
    @TableField("reservation_number")
    private Integer reservationNumber;
    /**
     * 审核日期
     */
    @TableField("date_of_audit")
    private Date dateOfAudit;
    /**
     *关联管理员账号id
     */
    @TableField("user_id")
    private String userId;
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
     * 签约
     * <ul>
     *     <li>1：审核已通过</li>
     *     <li>2：正在审核</li>
     *     <li>3：审核未通过</li>
     *     <li>4：签约未通过</li>
     * </ul>
     */
    private Integer reporting;

    private Integer grade;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
