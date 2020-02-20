package com.zhidejiaoyu.student.business.smallapp.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * 获取小程序码参数
 *
 * @author: wuchenxi
 * @date: 2020/2/20 15:46:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUnlimitedQRCodeDTO {

    /**
     * 小程序需要携带的参数
     */
    @NotEmpty(message = "scene can't be null")
    private String scene;

    /**
     * 扫码后跳转的页面路径，默认为主页
     */
    private String page;

    /**
     * 小程序码宽度，单位 px，最小 280px，最大 1280px
     */
    private Integer width;

    /**
     * 自动配置线条颜色，如果颜色依然是黑色，则说明不建议配置主色调，默认 false
     */
    private Boolean auto_color;

    /**
     * auto_color 为 false 时生效，使用 rgb 设置颜色 例如 {"r":"xxx","g":"xxx","b":"xxx"} 十进制表示
     */
    private JSONObject line_color;

    /**
     * 是否需要透明底色，为 true 时，生成透明底色的小程序
     */
    private Boolean is_hyaline;
}
