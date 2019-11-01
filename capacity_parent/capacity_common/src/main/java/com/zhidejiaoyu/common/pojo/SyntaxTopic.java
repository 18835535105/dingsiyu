package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
