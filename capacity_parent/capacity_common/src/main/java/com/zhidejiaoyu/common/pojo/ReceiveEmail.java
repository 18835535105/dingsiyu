package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 收件人邮箱
 * </p>
 *
 * @author zdjy
 * @since 2020-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ReceiveEmail extends Model<ReceiveEmail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 收件人姓名
     */
    private String name;
    /**
     * 收件人邮箱
     */
    private String email;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 注释
     */
    private String explain;
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
