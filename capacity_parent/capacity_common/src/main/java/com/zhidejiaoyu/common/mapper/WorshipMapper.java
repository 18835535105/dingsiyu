package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Worship;
import com.zhidejiaoyu.common.pojo.WorshipExample;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface WorshipMapper {
    int countByExample(WorshipExample example);

    int deleteByExample(WorshipExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Worship record);

    int insertSelective(Worship record);

    List<Worship> selectByExample(WorshipExample example);

    Worship selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Worship record, @Param("example") WorshipExample example);

    int updateByExample(@Param("record") Worship record, @Param("example") WorshipExample example);

    int updateByPrimaryKeySelective(Worship record);

    int updateByPrimaryKey(Worship record);

    /**
     * 查询用户一周的膜拜信息
     *
     * @param student
     * @return
     */
    List<Worship> selectSevenDaysInfoByStudent(@Param("student") Student student);

    /**
     * 查询上一个被膜拜的最高次数
     * @return
     */
    @Select("select DISTINCT count(w.id) from worship w,student s where w.student_id_by_worship = s.id and s.worship_first_time is not null GROUP BY s.id ")
    Integer countLastFirstCount();

    int getCountWorshipByStudentId(@Param("studentId") Long studentId);

    @MapKey("id")
    Map<Long,Map<String,Long>> getMapKeyStudentWorship();
}