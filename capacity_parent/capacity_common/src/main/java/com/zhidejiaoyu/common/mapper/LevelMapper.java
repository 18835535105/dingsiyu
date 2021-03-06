package com.zhidejiaoyu.common.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.zhidejiaoyu.common.pojo.Level;
import com.zhidejiaoyu.common.pojo.LevelExample;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelMapper extends BaseMapper<Level> {
    int countByExample(LevelExample example);

    int deleteByExample(LevelExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(Level record);

    List<Level> selectByExample(LevelExample example);

    Level selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Level record, @Param("example") LevelExample example);

    int updateByExample(@Param("record") Level record, @Param("example") LevelExample example);

    int updateByPrimaryKeySelective(Level record);

    int updateByPrimaryKey(Level record);

	List<Map<String, Object>> selectAll();

	@Select("select study_power from level where id=#{levelId}")
    Integer getStudyById(@Param("levelId") int level);
}
