package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Teacher;
import com.zhidejiaoyu.common.pojo.Voice;
import com.zhidejiaoyu.common.pojo.VoiceExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VoiceMapper extends BaseMapper<Voice> {
    int countByExample(VoiceExample example);

    int deleteByExample(VoiceExample example);

    int deleteByPrimaryKey(Long id);

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

    Integer selMaxCountByUnitIdAndStudentId(Map<String, Object> getMap);

    /**
     * 查询课文好声音全校排名
     */
    List<Map<String,Object>> selectTeksRankSchool(Map<String,Object> map);
    /**
     * 查询课文好声音全国排名
     */
    List<Map<String,Object>> selectTeksRank(Map<String,Object> map);

    /**
     * 获取好声音全校排行
     *
     * @param teachers
     * @param schoolAdminId
     * @param unitId
     * @param wordId
     * @param type
     * @return
     */
    List<Voice> selectSchoolRank(@Param("teachers") List<Teacher> teachers, @Param("schoolAdminId") Integer schoolAdminId, @Param("unitId") Long unitId, @Param("wordId") Long wordId, @Param("type") Integer type);

    /**
     * 查询所有没有班级的学生的好声音排行
     *
     * @param unitId
     * @param wordId
     * @param type
     * @return
     */
    List<Voice> selectTeacherIdIsNull(@Param("unitId") Long unitId, @Param("wordId") Long wordId, @Param("type") Integer type);

    List<Map<String,Object>> selVoiceTeksByStudentAndUnit(@Param("unitId") Long unitId,@Param("studentId") Long studentId);
}