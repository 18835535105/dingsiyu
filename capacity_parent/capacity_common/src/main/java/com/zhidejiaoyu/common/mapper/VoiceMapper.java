package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Teacher;
import com.zhidejiaoyu.common.pojo.Voice;
import com.zhidejiaoyu.common.pojo.VoiceExample;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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
     * @param createTime
     * @return
     */
    List<Voice> selectCountryRank(@Param("unitId") Long unitId, @Param("wordId") Long wordId, @Param("type") Integer type,
                                  @Param("createTime") Date createTime);

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
     *
     * @param map
     * @return
     */
    List<Map<String,Object>> selectTextRankSchool(@Param("map") Map<String,Object> map, @Param("host") String host);
    /**
     * 查询课文好声音全国排名
     *
     * @param map
     * @return
     */
    List<Map<String,Object>> selectTextRank(@Param("map") Map<String,Object> map, @Param("host") String host);

    /**
     * 获取好声音全校排行
     *
     * @param teachers
     * @param schoolAdminId
     * @param unitId
     * @param wordId
     * @param type
     * @param createTime
     * @return
     */
    List<Voice> selectSchoolRank(@Param("teachers") List<Teacher> teachers, @Param("schoolAdminId") Integer schoolAdminId,
                                 @Param("unitId") Long unitId, @Param("wordId") Long wordId, @Param("type") Integer type,
                                 @Param("createTime") Date createTime);

    /**
     * 查询所有没有班级的学生的好声音排行
     *
     * @param unitId
     * @param wordId
     * @param type
     * @param createTime
     * @return
     */
    List<Voice> selectTeacherIdIsNull(@Param("unitId") Long unitId, @Param("wordId") Long wordId, @Param("type") Integer type,
                                      @Param("createTime") Date createTime);

    List<Map<String,Object>> selVoiceTeksByStudentAndUnit(@Param("unitId") Long unitId,@Param("studentId") Long studentId);
}
