package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author zdjy
 * @since 2020-05-13
 * @deprecated 迁移到中台服务
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Deprecated
public class WeChat implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * openId
     */
    private String openId;

    /**
     * 微信名称
     */
    private String weChatName;

    /**
     * 微信图片
     */
    private String weChatImgUrl;


}
