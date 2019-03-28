package com.zhidejiaoyu.common.mapper;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import org.apache.ibatis.annotations.Select;
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

    @Update("update student_expansion set is_look=1 where id=#{studentId}")
    Integer updateByIsLook(Long studentId);

    StudentExpansion selectByStudentId(Long challengerStudentId);

}
