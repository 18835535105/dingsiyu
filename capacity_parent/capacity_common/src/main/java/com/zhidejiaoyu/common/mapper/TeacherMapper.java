package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Teacher;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 教师相关信息 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-12-11
 */
public interface TeacherMapper extends BaseMapper<Teacher> {

    /**
     * 根据教师id查询教师信息
     *
     * @param teacherId
     * @return
     */
    Teacher selectByTeacherId(Long teacherId);

    /**
     * 查询校管管辖的所有教师信息
     *
     * @param schoolAdminId
     * @return
     */
    List<Teacher> selectBySchoolAdminId(@Param("schoolAdminId") Integer schoolAdminId);

    /**
     * 查询当前教师所属的校管id
     *
     * @param teacherId
     * @return
     */
    Integer selectSchoolAdminIdByTeacherId(@Param("teacherId") Long teacherId);

    @Select("select school_admin_id from teacher where teacher_id = #{id}")
    Integer getSchoolAdminById(Integer id);

    Teacher selectTeacherBySchoolAdminId(@Param("schoolAdminId") Integer schoolAdminId);
}
