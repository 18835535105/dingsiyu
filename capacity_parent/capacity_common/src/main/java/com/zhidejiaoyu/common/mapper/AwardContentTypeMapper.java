package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.AwardContentType;
import com.zhidejiaoyu.common.pojo.AwardContentTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AwardContentTypeMapper {
    int countByExample(AwardContentTypeExample example);

    int deleteByExample(AwardContentTypeExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AwardContentType record);

    int insertSelective(AwardContentType record);

    List<AwardContentType> selectByExample(AwardContentTypeExample example);

    AwardContentType selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AwardContentType record, @Param("example") AwardContentTypeExample example);

    int updateByExample(@Param("record") AwardContentType record, @Param("example") AwardContentTypeExample example);

    int updateByPrimaryKeySelective(AwardContentType record);

    int updateByPrimaryKey(AwardContentType record);
}