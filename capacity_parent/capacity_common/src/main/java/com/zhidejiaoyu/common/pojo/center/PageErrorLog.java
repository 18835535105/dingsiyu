package com.zhidejiaoyu.common.pojo.center;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 前端页面错误信息收集表
 * </p>
 *
 * @author zdjy
 * @since 2020-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PageErrorLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 用户信息
     */
    private String userInfo;

    /**
     * 错误信息
     */
    private String errMsg;

    /**
     * 错误页面的路由
     */
    private String route;

    /**
     * 错误行数
     */
    private String errorLine;

    /**
     * 错误详细信息
     */
    private String errDetail;

    /**
     * 错误发生时间
     */
    private Date errTime;


}
