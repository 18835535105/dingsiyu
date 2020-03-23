package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 课文表
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Teks extends Model<Teks> {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /**
     * 课文句子
     */
    private String sentence;
    /**
     * 对话人
     */
    private String speaker;
    /**
     * 翻译
     */
    private String paraphrase;
    /**
     * 单元id
     */
    @TableField("unit_id")
    private Integer unitId;
    /**
     * 读音
     */
    private String pronunciation;
    /**
     * 状态 1,开启   2,删除
     */
    private Integer status;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
