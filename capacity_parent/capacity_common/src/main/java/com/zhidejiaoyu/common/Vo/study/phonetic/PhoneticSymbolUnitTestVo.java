package com.zhidejiaoyu.common.Vo.study.phonetic;

import lombok.Data;

import java.io.Serializable;

/**
 * 音标单元闯关测试
 *
 * @author wuchenxi
 * @date 2019-05-20
 */
@Data
public class PhoneticSymbolUnitTestVo implements Serializable {

    /**
     * 音标
     */
    private String phonetic;

    /**
     * 测试类别
     */
    private String type;


}
