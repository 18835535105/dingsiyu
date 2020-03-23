package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2019-12-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LearnNew extends Model<LearnNew> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long courseId;
    /**
     * 单元id
     */
    private Long unitId;
    /**
     * 分组
     */
    @TableField("`group`")
    private Integer group;
    /**
     * 难易类型：1:简单类型；2:难类型
     */
    private Integer easyOrHard;

    /**
     * 1：单词；2：句型；3：课文；4：语法
     */
    private Integer modelType;

    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
