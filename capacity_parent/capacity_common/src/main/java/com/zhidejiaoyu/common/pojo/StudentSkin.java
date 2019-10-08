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
 * 学生皮肤表
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StudentSkin extends Model<StudentSkin> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 学生id
     */
    private Integer studentId;
    /**
     * 皮肤名称
     */
    private String skinName;
    /**
     * 到期时间  试用皮肤与正式皮肤使用到期时间
     */
    private Date endTime;
    /**
     * 皮肤使用状态
     */
    private Integer state;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 皮肤地址
     */
    private String imgUrl;

    /**
     * 1，正式拥有  2，试用皮肤
     */
    private Integer type;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
