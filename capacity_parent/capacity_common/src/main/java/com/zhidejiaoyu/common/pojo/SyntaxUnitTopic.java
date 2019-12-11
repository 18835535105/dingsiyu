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
 * @since 2019-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SyntaxUnitTopic extends Model<SyntaxUnitTopic> {

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
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
