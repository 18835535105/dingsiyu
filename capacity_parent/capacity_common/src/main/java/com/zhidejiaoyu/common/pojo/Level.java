package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class Level extends Model<Level> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String levelName;

    private String childName;

    private Long nextId;

    private Integer gold;

    private String imgUrl;

    /**
     * 等级图片，用于学生查看等级和勋章的页面展示
     */
    private String imgUrlLevel;

    /**
     * 等级金色文字图片
     */
    private String imgUrlWord;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}