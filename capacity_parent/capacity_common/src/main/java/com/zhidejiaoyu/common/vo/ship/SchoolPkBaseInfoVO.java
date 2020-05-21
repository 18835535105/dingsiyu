package com.zhidejiaoyu.common.vo.ship;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 可以挑战的校区副本信息
 *
 * @author: wuchenxi
 * @date: 2020/5/21 10:19:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolPkBaseInfoVO implements Serializable {

    private Long id;

    private String imgUrl;

    private String name;
}
