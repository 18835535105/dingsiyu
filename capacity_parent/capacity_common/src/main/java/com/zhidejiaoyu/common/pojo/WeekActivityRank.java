package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 每周活动结果排名
 * </p>
 *
 * @author zdjy
 * @since 2020-05-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WeekActivityRank implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 活动id
     */
    private Integer weekActivityConfigId;

    /**
     * 活动期间完成内容
     */
    private Integer complateCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 1：校区排行；2：同服务器排行
     */
    private Integer type;


}
