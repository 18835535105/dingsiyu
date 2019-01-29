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
 * 抽取的碎片记录表
 * </p>
 *
 * @author zdjy
 * @since 2018-11-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Exhumation extends Model<Exhumation> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 学生id
     */
    private Integer studentId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 碎片名
     */
    private String name;
    /**
     * 1,手套  2,花瓣  3,皮肤碎片 
     */
    private Integer type;
    /**
     * 碎片图片地址
     */
    private String imgUrl;
    /**
     * 碎片合成状态
     */
    private Integer state;
    /**
     * 最终合成奖励的名字
     */
    private String finalName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
