package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
 * @since 2019-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PhoneticSymbol extends Model<PhoneticSymbol> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 音标
     */
    private String phoneticSymbol;
    /**
     * 类型
     */
    private String type;
    /**
     * 对应字母
     */
    private String letter;
    /**
     * 发音方法
     */
    private String pronunciationMethod;
    /**
     * 音标读音地址
     */
    private String url;

    private Integer unitId;

    private String content;

    private String partUrl;

    private Integer status;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
