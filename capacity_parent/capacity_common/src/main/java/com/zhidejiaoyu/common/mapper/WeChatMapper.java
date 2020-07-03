package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.WeChat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-05-13
 * @deprecated 迁移到中台
 */
@Deprecated
@Repository("weChatMapper1")
public interface WeChatMapper extends BaseMapper<WeChat> {

    WeChat selectByOpenId(@Param("openId") String openId);
}
