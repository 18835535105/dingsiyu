package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Partner;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-05-13
 */
public interface PartnerMapper extends BaseMapper<Partner> {

    Integer countByOpenId(@Param("openId") String openId);

    void deleteByOpenId(@Param("openId") String openId);
}
