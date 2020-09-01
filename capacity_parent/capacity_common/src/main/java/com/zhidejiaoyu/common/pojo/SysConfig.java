package com.zhidejiaoyu.common.pojo;


import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统配置文件
 * </p>
 *
 * @author zdjy
 * @since 2020-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysConfig implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 配置内容
     */
    private String content;

    /**
     * 配置说明
     */
    @TableField("`explain`")
    private String explain;

    /**
     * 配置更新时间
     */
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
