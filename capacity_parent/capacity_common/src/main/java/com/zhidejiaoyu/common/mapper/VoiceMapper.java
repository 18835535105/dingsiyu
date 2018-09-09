package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Voice;
import com.zhidejiaoyu.common.pojo.VoiceExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VoiceMapper {
    int countByExample(VoiceExample example);

    int deleteByExample(VoiceExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Voice record);

    int insertSelective(Voice record);

    List<Voice> selectByExample(VoiceExample example);

    Voice selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Voice record, @Param("example") VoiceExample example);

    int updateByExample(@Param("record") Voice record, @Param("example") VoiceExample example);

    int updateByPrimaryKeySelective(Voice record);

    int updateByPrimaryKey(Voice record);

    /**
     * 获取好声音全国排行表
     *
     * @param unitId
     * @param wordId
     * @param type
     * @return
     */
    List<Voice> selectCountryRank(@Param("unitId") Long unitId, @Param("wordId") Long wordId, @Param("type") Integer type);

    /**
     * 获取班级排名
     *
     * @param student
     * @param unitId
     * @param wordId
     * @param type
     * @return
     */
    List<Voice> selectClassRank(@Param("student") Student student, @Param("unitId") Long unitId, @Param("wordId") Long wordId, @Param("type") Integer type);
}