package com.zhidejiaoyu.common.mapper.center;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.center.Partner;
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
