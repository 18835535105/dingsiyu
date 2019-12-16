package com.zhidejiaoyu.common.vo.simple.ccieVo;

import lombok.Data;

import java.util.List;

/**
 * 个人中心我的课程vo
 *
 * @author wuchenxi
 * @date 2018/9/12
 */
@Data
public class MyCcieVo {

    private String testName;

    /**
     * 证书获取时间
     */
    private String getTime;

    /**
     * 证书类型名称
     */
    private String ccieTypeName;

    private List<CcieVo> ccieVos;

}
