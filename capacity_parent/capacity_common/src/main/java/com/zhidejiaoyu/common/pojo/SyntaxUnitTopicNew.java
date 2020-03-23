package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 超级语法单元-内容表

 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SyntaxUnitTopicNew extends Model<SyntaxUnitTopicNew> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 超级语法单元id
     */
    private Long unitId;
    /**
     * 超级语法内容id
     */
    private Long topicId;
    /**
     * 语法题在单元中的组序号
     */
    @TableField("`group`")
    private Integer group;
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
