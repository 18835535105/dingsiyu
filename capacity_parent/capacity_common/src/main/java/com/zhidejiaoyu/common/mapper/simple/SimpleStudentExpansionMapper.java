package com.zhidejiaoyu.common.mapper.simple;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生扩展内容表； Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-15
 */
@Repository
public interface SimpleStudentExpansionMapper extends BaseMapper<StudentExpansion> {

    StudentExpansion isHave(@Param("studentId") Long studentId);

    void addStudy(@Param("studentId") Long studentId, @Param("study") Integer study,
                  @Param("level") int level, @Param("isLook") int isLook);

    @Update("update student_expansion set is_look=1 where id=#{studentId}")
    Integer updateByIsLook(Long studentId);

    StudentExpansion selectByStudentId(Long challengerStudentId);

    @Update("update student_expansion set pk_explain=2 where id=#{studentId}")
    Integer updatePkExplain(@Param("studentId") Long studnetId);

    List<Map<String, Object>> getMaxStudyTwenty(@Param("classId") Long classId, @Param("teacherId") Long teacherId, @Param("teachers") List<Integer> teachers, @Param("schoolAdminId") Integer schoolAdminId, @Param("type") Integer type);

    List<StudentExpansion> selectAll();

    List<Long> selectSourcePowerSortByStudentIds(@Param("studentIds") List<Long> studentIds, @Param("sort") Integer sort);

    List<Long> selectPkNumSortByStudentIds(@Param("studentIds") List<Long> studentIds, @Param("sort") Integer sort);
}
