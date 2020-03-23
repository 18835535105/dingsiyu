package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 微信抽奖
 * </p>
 *
 * @author zdjy
 * @since 2020-02-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StudentPayConfig extends Model<StudentPayConfig> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 微信id
     */
    private String wenXinId;
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 奖品id
     */
    private Long prizeConfigId;
    /**
     * 1，未领取，2已领取，3校管删除
     */
    private Integer type;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 获取奖品码
     *
     * @return
     */
    private String obtain;

    /**
     * 微信头像地址
     * @return
     */
    private String weChatImgUrl;

    /**
     * 微信名称
     * @return
     */
    private String weChatName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
