package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

/**
 * <p>
 * 学生排名统计表
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-02
 */
@Data
public class Ranking {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 金币班级排名
     */
    @TableField("gold_class_rank")
    private Integer goldClassRank;
    /**
     * 金币学校排名
     */
    @TableField("gold_school_rank")
    private Integer goldSchoolRank;
    /**
     * 金币全国排名
     */
    @TableField("gold_country_rank")
    private Integer goldCountryRank;
    /**
     * 学生id
     */
    @TableField("student_id")
    private Long studentId;
    /**
     * 膜拜班级排名
     */
    @TableField("worship_class_rank")
    private Integer worshipClassRank;
    /**
     * 膜拜学校排名
     */
    @TableField("worship_school_rank")
    private Integer worshipSchoolRank;
    /**
     * 膜拜全国排名
     */
    @TableField("worship_country_rank")
    private Integer worshipCountryRank;

}
