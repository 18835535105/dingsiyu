package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.AwardContentType;
import com.zhidejiaoyu.common.pojo.AwardContentTypeExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SimpleAwardContentTypeMapper extends BaseMapper<AwardContentType> {
    int countByExample(AwardContentTypeExample example);

    int deleteByExample(AwardContentTypeExample example);

    int deleteByPrimaryKey(Integer id);


    int insertSelective(AwardContentType record);

    List<AwardContentType> selectByExample(AwardContentTypeExample example);

    AwardContentType selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AwardContentType record, @Param("example") AwardContentTypeExample example);

    int updateByExample(@Param("record") AwardContentType record, @Param("example") AwardContentTypeExample example);

    int updateByPrimaryKeySelective(AwardContentType record);

    int updateByPrimaryKey(AwardContentType record);

    /**
     * 批量查询
     *
     * @param ids
     * @return
     */
    List<AwardContentType> selectByIds(@Param("ids") List<Long> ids);
}
