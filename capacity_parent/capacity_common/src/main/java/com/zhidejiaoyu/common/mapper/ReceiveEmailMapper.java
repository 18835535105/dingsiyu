package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.ReceiveEmail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 收件人邮箱 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-01-15
 */
@Deprecated
@Repository("ReceiveEmail01")
public interface ReceiveEmailMapper extends BaseMapper<ReceiveEmail> {

    /**
     * 查询指定type记录
     *
     * @param type
     * @return
     */
    List<ReceiveEmail> selectByType(@Param("type") int type);
}
