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
    private Integer group;
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
