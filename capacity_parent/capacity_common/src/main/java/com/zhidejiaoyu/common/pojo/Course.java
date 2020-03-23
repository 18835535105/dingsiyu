package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 课程
 * 
 * @author Administrator
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Course extends Model<Course> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学段 */
    private String studyParagraph;

    /** 年级 */
    private String grade;

    /** 版本 */
    private String version;

    /** 标签 */
    private String label;

    /** 课本封面图名     
     1.年级 取首字母
     2.版本 取首字母
     3.标签 取首字母
         123拼接成的图片名*/
    private String pictureName;
    
    /** 封面图url全地址 */
    private String pictureUrl;

    /** 课程名 */
    private String courseName;

    /** 删除状态 */
    @TableField("delStatus")
    private int delStatus;
    
	/** ! 单元数 */
	@TableField(exist = false)
    private int unitCount;
    /** ! 开启状态中文 */
    @TableField(exist = false)
    private String stat;

	/** 状态 */
    private Integer status;

    /** 创建时间 */
    private String createTime;

    /** 更新时间 */
    private Date updateTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}