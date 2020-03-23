package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.ShareConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-02-14
 */
public interface ShareConfigMapper extends BaseMapper<ShareConfig> {

    String selectImgByAdminId(@Param("adminId") Integer adminId);

    ShareConfig selectByAdminId(@Param("adminId") Integer adminId);
}
