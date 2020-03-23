package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
 * @since 2019-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SyntaxTopicNew extends Model<SyntaxTopicNew> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 语法知识点id
     */
    private Long knowledgePointId;
    /**
     * 题干（题目），其中 $&$ 符号为占位符，选择题类型中为选项占位，填空题为挖空位置
     */
    private String topic;
    /**
     * 答案
     */
    private String answer;
    /**
     * 1,选择题 2,填空题
     */
    private Integer type;
    private Date updateTime;
    /**
     * 选项（只有选择题使用）
     */
    private String option;
    /**
     * 解析
     */
    private String analysis;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
