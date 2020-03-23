package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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

    private Integer count;

    /**
     * 1:流程内单词好声音；2：流程外单词好声音
     */
    private Integer flag;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}