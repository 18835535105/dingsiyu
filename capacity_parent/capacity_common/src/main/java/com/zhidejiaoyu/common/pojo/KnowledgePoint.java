package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 知识点表
 * </p>
 *
 * @author zdjy
 * @since 2019-10-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KnowledgePoint extends Model<KnowledgePoint> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 知识点名称
     */
    private String name;
    /**
     * 知识点内容
     */
    private String content;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
