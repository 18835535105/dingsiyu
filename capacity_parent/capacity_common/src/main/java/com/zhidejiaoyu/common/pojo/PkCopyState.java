package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private Integer schoolAdminId;

    /**
     * 副本信息id
     */
    private Long pkCopyBaseId;

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

    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
