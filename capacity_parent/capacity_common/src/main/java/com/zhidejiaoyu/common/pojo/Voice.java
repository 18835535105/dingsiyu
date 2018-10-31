package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class Voice extends Model<Voice> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long courseId;

    private Long unitId;

    private Long wordId;

    private Integer type;

    private Double score;

    private String voiceUrl;

    private Date createTime;

    private String studentName;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}