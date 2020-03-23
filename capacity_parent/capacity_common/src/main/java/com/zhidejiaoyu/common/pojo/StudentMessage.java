package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 个人中心—》消息中心—》消息通知表
 * </p>
 *
 * @author zdjy
 * @since 2018-12-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StudentMessage extends Model<StudentMessage> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Long teacherId;
    private Long studentId;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 收件人id
     */
    private Integer receiverId;
    /**
     * 收件人姓名
     */
    private String receiverName;
    /**
     * 收件人账号
     */
    private String receiverAccount;
    /**
     * 消息状态：1：正常；2：删除；3：撤回
     */
    private Integer state;
    private Date createTime;

    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
