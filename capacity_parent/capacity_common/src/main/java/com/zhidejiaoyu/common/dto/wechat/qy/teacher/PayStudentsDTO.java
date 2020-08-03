package com.zhidejiaoyu.common.dto.wechat.qy.teacher;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量充值
 *
 * @author wuchenxi
 * @date 2020-08-03 18:05:59
 */
@Data
public class PayStudentsDTO {

    /**
     * 学生uuids
     */
    List<String> studentIds;

    /**
     * 充几个月的课时
     */
    @Min(value = 1,  message = "请选择充课课时")
    Integer type;

    /**
     * 学管openId
     */
    @NotNull(message = "openId can't be null!")
    String openId;
}
