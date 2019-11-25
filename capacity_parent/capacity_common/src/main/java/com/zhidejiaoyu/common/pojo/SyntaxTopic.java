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
 * @since 2019-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SyntaxTopic extends Model<SyntaxTopic> {

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
     * 选择题
     */
    private String option;
    /**
     * 解析
     * @return
     */
    private String analysis;

    /**
     * 判断是否有（）类型1，没有2，有
     */
    private Integer model;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
