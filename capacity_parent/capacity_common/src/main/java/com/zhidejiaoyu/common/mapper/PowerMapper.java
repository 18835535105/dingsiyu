package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Power;
import com.zhidejiaoyu.common.pojo.PowerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PowerMapper {
    int countByExample(PowerExample example);

    int deleteByExample(PowerExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Power record);

    int insertSelective(Power record);

    List<Power> selectByExample(PowerExample example);

    Power selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Power record, @Param("example") PowerExample example);

    int updateByExample(@Param("record") Power record, @Param("example") PowerExample example);

    int updateByPrimaryKeySelective(Power record);

    int updateByPrimaryKey(Power record);
}