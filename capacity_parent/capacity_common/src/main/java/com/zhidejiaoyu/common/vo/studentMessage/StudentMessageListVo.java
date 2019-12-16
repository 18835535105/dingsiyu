package com.zhidejiaoyu.common.vo.studentMessage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhidejiaoyu.common.pojo.StudentMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息通知列表显示内容
 *
 * @author wuchenxi
 * @date 2018-12-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StudentMessageListVo extends StudentMessage {
    /**
     * 学生头像路径
     */
    private String headUrl;

    /**
     * 消息创建的毫秒值
     */
    private Long timestamp;
}
