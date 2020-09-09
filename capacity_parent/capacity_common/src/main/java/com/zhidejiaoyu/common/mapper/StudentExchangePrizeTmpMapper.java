package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.StudentExchangePrizeTmp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 迁移数据时，用于临时存放学生兑奖记录，然后通过奖品名称将这些记录放到兑奖记录正式表中 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-09-09
 */
public interface StudentExchangePrizeTmpMapper extends BaseMapper<StudentExchangePrizeTmp> {

}
