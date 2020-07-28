package com.zhidejiaoyu.common.mapper.center;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.center.WechatVideo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-07-27
 */
public interface WechatVideoMapper extends BaseMapper<WechatVideo> {

    /**
     * 查询下个应学习的视频
     *
     * @param userUuid
     * @param grade
     * @return
     */
    WechatVideo selectNextVideo(@Param("userUuid") String userUuid, @Param("grade") String grade);
}
