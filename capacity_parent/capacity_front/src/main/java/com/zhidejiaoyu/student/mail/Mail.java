package com.zhidejiaoyu.student.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: wuchenxi
 * @Date: 2019/11/6 16:38
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mail {
    /**
     * 接收者
     */
    private String[] to;

    /**
     * 主题
     */
    private String subject;
    /**
     * 内容
     */
    private String content;

    /**
     * 附件地址
     */
    private String filePath;
}
