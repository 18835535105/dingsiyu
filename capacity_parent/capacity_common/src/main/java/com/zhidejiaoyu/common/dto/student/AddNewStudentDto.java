package com.zhidejiaoyu.common.dto.student;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 批量生成学生账号 dto
 *
 * @author wuchenxi
 * @date 2019-05-08
 */
@Data
public class AddNewStudentDto {

    @Min(value = 1, message = "生成账号数量最少为 1 ！")
    @NotNull(message = "生成账号数量不能为空！")
    private Integer count;

    @Max(value = 365, message = "账号有效期最多为 365 天！")
    @Min(value = 1, message = "账号有效期最少为 1 天！")
    @NotNull(message = "账号有效期限不能为空！")
    private Integer validity;

    private String schoolName;

    /**
     * 福利账号中是“智慧单词”的值
     * 正常账号中是“学段”字段的值
     */
    @NotBlank(message = "请选择账号所属学段！")
    private String phase;
    /**
     * 学生学习的版本
     */
    private String version;
    /**
     * 学生年级
     */
    private String grade;

    /**
     * 学管openId
     */
    private String openId;
}
