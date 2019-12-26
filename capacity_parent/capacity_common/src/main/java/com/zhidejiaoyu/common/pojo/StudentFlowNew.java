package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StudentFlowNew extends Model<StudentFlowNew> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long studentId;
    /**
     * 当前节点id
     */
    private Long currentFlowId;
    /**
     * 单元id
     */
    private Long unitId;
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
