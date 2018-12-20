package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 学生测试详情
 * </p>
 *
 * @author zdjy
 * @since 2018-10-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TestRecordInfo extends Model<TestRecordInfo> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 测试记录id
     */
    private Long testId;
    /**
     * 学生选择的答案
     */
    private String selected;
    /**
     * 试题中的题目单词
     */
    private String word;
    /**
     * 选项a
     */
    private String optionA;
    /**
     * 选项b
     */
    private String optionB;
    /**
     * 选项c
     */
    private String optionC;
    /**
     * 选项d
     */
    private String optionD;
    /**
     * 正确答案
     */
    private String answer;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
