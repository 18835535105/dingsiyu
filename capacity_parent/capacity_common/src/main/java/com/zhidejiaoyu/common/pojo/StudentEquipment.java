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
 * @since 2020-02-27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StudentEquipment extends Model<StudentEquipment> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 装备id
     */
    private Long equipmentId;
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 飞船强化程度（强化次数）
     */
    private Integer intensificationDegree;
    /**
     * 1装备 2未装备
     */
    private Integer type;

    private Date createTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
