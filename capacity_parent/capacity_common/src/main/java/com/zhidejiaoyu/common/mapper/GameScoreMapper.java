package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.GameScore;
import com.zhidejiaoyu.common.pojo.GameScoreExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GameScoreMapper {
    int countByExample(GameScoreExample example);

    int deleteByExample(GameScoreExample example);

    int deleteByPrimaryKey(Long id);

    int insert(GameScore record);

    int insertSelective(GameScore record);

    List<GameScore> selectByExample(GameScoreExample example);

    GameScore selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") GameScore record, @Param("example") GameScoreExample example);

    int updateByExample(@Param("record") GameScore record, @Param("example") GameScoreExample example);

    int updateByPrimaryKeySelective(GameScore record);

    int updateByPrimaryKey(GameScore record);
}