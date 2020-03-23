package com.zhidejiaoyu.common.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 学生扩展内容表； Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-15
 */
@Repository
public interface StudentExpansionMapper extends BaseMapper<StudentExpansion> {

    @Update("update student_expansion set is_look=1 where id=#{studentId}")
    Integer updateByIsLook(Long studentId);

    StudentExpansion selectByStudentId(Long challengerStudentId);

    /**
     * 查询学校下所有学生的扩展信息
     *
     * @param schoolAdminId
     * @return
     */
    List<StudentExpansion> selectBySchoolAdminId(@Param("schoolAdminId") Integer schoolAdminId);
}
