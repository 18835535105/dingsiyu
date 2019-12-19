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
 * @since 2019-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SchoolTime extends Model<SchoolTime> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * type=1校长id ；type=2学生id
     */
    private Long userId;
    /**
     * 年级
     */
    private String grade;
    /**
     * 单元id
     */
    private Long unitId;
    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 月份
     */
    private Integer month;
    /**
     * 周
     */
    private Integer week;
    private Date updateTime;
    /**
     * 1校长 2学生
     */
    private Integer type;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
