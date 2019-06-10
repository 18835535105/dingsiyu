package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.GameStore;
import com.zhidejiaoyu.common.pojo.GameStoreExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GameStoreMapper {
    int countByExample(GameStoreExample example);

    int deleteByExample(GameStoreExample example);

    int deleteByPrimaryKey(Long id);

    int insert(GameStore record);

    int insertSelective(GameStore record);

    List<GameStore> selectByExample(GameStoreExample example);

    GameStore selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") GameStore record, @Param("example") GameStoreExample example);

    int updateByExample(@Param("record") GameStore record, @Param("example") GameStoreExample example);

    int updateByPrimaryKeySelective(GameStore record);

    int updateByPrimaryKey(GameStore record);
}
