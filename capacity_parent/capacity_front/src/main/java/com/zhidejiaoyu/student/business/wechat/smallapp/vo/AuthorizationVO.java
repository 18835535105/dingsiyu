package com.zhidejiaoyu.student.business.wechat.smallapp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 授权数据
 *
 * @author: wuchenxi
 * @date: 2020/2/19 13:49:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationVO implements Serializable {

    private String sessionKey;

    private String openId;
}
