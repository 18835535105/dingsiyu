package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 队长阅读与英语乐园文章表
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("read_arder")
public class ReadArder extends Model<ReadArder> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 阅读类型  （0,英语乐园    1,队长阅读选择题 2,队长阅读判断题）
     */
    private Integer type;
    /**
     * 短文句子
     */
    private String sentence;
    /**
     * 翻译
     */
    private String translate;
    /**
     * 课程id
     */
    @TableField("course_id")
    private Long courseId;
    /**
     * 1,队长讲阅读  2,开心一刻 3,迷你谜语 4,队长游世界
     */
    @TableField("text_types")
    private Integer textTypes;
    /**
     * 图片地址
     */
    @TableField("part_url")
    private String partUrl;
    /**
     * 标题
     */
    private String title;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
