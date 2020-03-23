package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.AwardContentType;
import com.zhidejiaoyu.common.pojo.AwardContentTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AwardContentTypeMapper extends BaseMapper<AwardContentType> {
    /**
     * 批量查询
     *
     * @param ids
     * @return
     */
    List<AwardContentType> selectByIds(@Param("ids") List<Long> ids);}