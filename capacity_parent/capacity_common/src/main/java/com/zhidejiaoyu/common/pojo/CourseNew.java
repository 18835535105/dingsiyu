package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 课程表
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CourseNew extends Model<CourseNew> {

    private static final long serialVersionUID = 1L;

    /**
     * 课程表主建
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 学段
     */
    private String studyParagraph;
    /**
     * 年级
     */
    private String grade;
    /**
     * 版本
     */
    private String version;
    /**
     * 标签
     */
    private String label;
    /**
     * 年级不是普通形式时，在该列将年级转化为普通形式
     */
    private String gradeExt;
    /**
     * 只有上册下册全册
     */
    private String labelExt;
    /**
     * 课本封面图url地址
     */
    private String pictureUrl;
    /**
     * 课程名
     */
    private String courseName;
    /**
     * 状态, 1=开启(默认), 2=关闭
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 删除状态: 1:未删除(默认)  2:已删除
     */
    @TableField("delStatus")
    private Integer delStatus;




    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
