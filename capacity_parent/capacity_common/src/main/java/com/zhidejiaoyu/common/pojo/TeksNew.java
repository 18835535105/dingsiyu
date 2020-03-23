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
 * 课文表
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TeksNew extends Model<TeksNew> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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
     * 读音
     */
    private String pronunciation;
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
