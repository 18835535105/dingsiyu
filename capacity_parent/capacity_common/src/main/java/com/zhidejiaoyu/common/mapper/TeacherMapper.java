package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Teacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

    @Select("select school_admin_id from teacher where teacher_id = #{id} or school_admin_id = #{id} limit 1")
    Integer getSchoolAdminById(Integer id);

    Teacher selectTeacherBySchoolAdminId(@Param("schoolAdminId") Integer schoolAdminId);

    List<Long> selectAllAdminId();

    /**
     * 查询校管密码
     *
     * @param id
     * @return
     */
    @Select("select password from teacher where school_admin_id = #{id} limit 1")
    String selectPasswordBySchoolAdminId(@Param("id") Integer id);

    /**
     * 查询教师密码
     *
     * @param id
     * @return
     */
    @Select("select password from teacher where teacher_id = #{id} limit 1")
    String selectPasswordByTeacherId(@Param("id") Integer id);

    /**
     * 查询教师密码
     *
     * @param id
     * @return
     */
    @Select("select password from teacher where teacher_id = #{id} limit 1")
    String countByTeacherId(@Param("id") Integer id);

    /**
     * 根据校管id获取校管信息
     *
     * @param schoolAdminId
     * @return
     */
    Teacher selectSchoolAdminById(@Param("schoolAdminId") Integer schoolAdminId);

    @Select("select teacher_id from teacher where school_admin_id =#{adminId} and teacher_id is not null")
    List<Long> selectTeacherIdsBySchoolAdminId(Integer adminId);

    @Select("select school from teacher where school_admin_id =#{adminId} or teacher_id =#{adminId} limit 1")
    String selectSchoolById(@Param("adminId") Long schoolId);
}
