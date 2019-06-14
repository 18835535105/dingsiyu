package com.zhidejiaoyu.common.Vo.simple.studentInfoVo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 点击勋章时显示当前勋章获取的个数
 *
 * @author wuchenxi
 * @date 2018/10/9
 */
@Data
public class ChildMedalVo implements Serializable {

    private List<String> medalImgUrl;

    /**
     * 勋章说明
     */
    private String content;
}
