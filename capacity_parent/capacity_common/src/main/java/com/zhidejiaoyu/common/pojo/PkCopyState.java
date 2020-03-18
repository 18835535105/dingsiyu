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
 *
 * </p>
 *
 * @author zdjy
 * @since 2020-03-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PkCopyState extends Model<PkCopyState> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 校管id
     */
    private Long schoolAdminId;

    /**
     * 副本信息id
     */
    private Integer pkCopyBaseId;

    /**
     * 剩余耐久度
     */
    private Integer durability;
    /**
     * 1：单人副本；2：校区副本
     */
    private Integer type;
    /**
     * 挑战结束时间
     */
    private Date createTime;
    /**
     * bossId
     */
    private Long pkCopyBaseId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
