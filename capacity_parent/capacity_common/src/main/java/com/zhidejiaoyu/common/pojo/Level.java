package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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

    /**
     * 学习力
     */
    private Integer studyPower;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
