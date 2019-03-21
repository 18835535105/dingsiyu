package com.zhidejiaoyu.common.mapper;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 学生扩展内容表； Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-15
 */
public interface StudentExpansionMapper extends BaseMapper<StudentExpansion> {

    StudentExpansion isHave(@Param("studentId") Long studentId);

    void addStudy(@Param("studentId") Long studentId, @Param("study") Integer study,
                  @Param("level") int level, @Param("isLook") int isLook);

    @Update("update student_expansion set is_look=1 where id=#{studentId}")
    Integer updateByIsLook(Long studentId);

    StudentExpansion selectByStudentId(Long challengerStudentId);
}
