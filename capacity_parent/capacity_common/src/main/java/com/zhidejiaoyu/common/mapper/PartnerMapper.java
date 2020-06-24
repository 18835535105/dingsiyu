package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Partner;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-05-13
 * @deprecated 放到中台数据库中
 */
@Deprecated
@Repository("PartnerMapper1")
public interface PartnerMapper extends BaseMapper<Partner> {

    Integer countByOpenId(@Param("openId") String openId);

    void deleteByOpenId(@Param("openId") String openId);
}
