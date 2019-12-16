package com.zhidejiaoyu.common.vo.simple.testVo;

import com.zhidejiaoyu.common.pojo.TestRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 测试记录页面展示数据
 *
 * @author wuchenxi
 * @date 2019-02-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TestRecordVo extends TestRecord {

    private String courseName;

    private String unitName;
}
