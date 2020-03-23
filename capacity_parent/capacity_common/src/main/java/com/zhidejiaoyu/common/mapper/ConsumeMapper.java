package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Consume;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-11-21
 */
public interface ConsumeMapper extends BaseMapper<Consume> {

    Integer getAllGoladAndDiamond(Consume consume);

}
