package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 课程表
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
@Data
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
     * 课本封面图名
     */
    private String pictureName;
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
