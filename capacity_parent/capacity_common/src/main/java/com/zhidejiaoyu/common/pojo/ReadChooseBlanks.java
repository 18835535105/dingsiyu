package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 阅读选句填空答案表
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("read_choose_blanks")
public class ReadChooseBlanks extends Model<ReadChooseBlanks> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 阅读类型表id
     */
    @TableField("read_type_id")
    private Long readTypeId;
    /**
     * 选项排列顺序要求（正确顺序排列，最后为错误答案）
     */
    private String content;
    /**
     * 解析
     */
    private String analysis;

    /**
     * 答案正确顺序
     */
    @TableField("reight_order")
    private String reightOrder;
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
