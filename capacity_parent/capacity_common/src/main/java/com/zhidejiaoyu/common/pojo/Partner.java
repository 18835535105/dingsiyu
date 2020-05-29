package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author zdjy
 * @since 2020-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Partner implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * openId
     */
    @TableField("openId")
    private String openId;

    private String nickname;

    /**
     * 头像地址
     */
    private String imgUrl;

    /**
     * 分数
     */
    private Integer totalSorce;

    /**
     * 类型
     */
    private String type;

    /**
     * 经济价值
     */
    private Integer economicValue;

    /**
     * 超过人数
     */
    private Double overPerson;

    private Date createTime;

}
