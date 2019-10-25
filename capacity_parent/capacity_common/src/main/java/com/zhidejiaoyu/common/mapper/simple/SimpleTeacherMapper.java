package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Teacher;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 教师相关信息 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-09-08
 */
public interface SimpleTeacherMapper extends BaseMapper<Teacher> {

    /**
     * 获取教师的学校
     *
     * @return
     */
    @Select("select distinct school from teacher where school is not null and school <> ''")
    List<String> selectSchools();

    /**
     * 根据教师id查询教师信息
     *
     * @param userId
     * @return
     */
    Teacher selectByTeacherId(@Param("userId") Integer userId);

    /**
     * 根据校管id获取校管信息
     *
     * @param schoolAdminId
     * @return
     */
    Teacher selectSchoolAdminById(@Param("schoolAdminId") Integer schoolAdminId);

    /**
     * 查找当前登录人员的学校名称
     *
     * @param id
     * @return
     */
    @Select("select school from teacher where teacher_id = #{id} or school_admin_id = #{id} limit 1")
    String selectSchoolName(Integer id);

    List<Map<String, Object>> selTeacherBYAdminId(Integer adminId);

    List<Map<String, Object>> selGradeByAdminId(Integer adminId);

    /**
     * 获取校管下所有教师信息
     *
     * @param schoolAdminId
     * @return
     */
    List<Teacher> selectTeachersBySchoolAdminId(@Param("schoolAdminId") Integer schoolAdminId);

    /**
     * 更新教师开通平台
     *
     * @param teachers
     * @param platform
     */
    void updatePlatform(@Param("teachers") List<Teacher> teachers, @Param("platform") Integer platform);

    @Select("select school_admin_id from teacher where teacher_id = #{id}")
    Integer getSchoolAdminById(Integer id);

    @Select("select school from teacher where school_admin_id = #{id} group by school")
    List<String> selectSchoolsBySchoolAdminId(Integer id);

    /**
     * 更新校管及其下属教师开通平台权限
     *
     * @param teacher
     * @param platform
     */
    @Update("update teacher set platform = #{platform} where school_admin_id = #{teacher.schoolAdminId}")
    void updateBySchoolAdminIdId(@Param("teacher") Teacher teacher, @Param("platform") Integer platform);

    /**
     * 查询教师所属的校管 id
     *
     * @param teacherId
     * @return
     */
    @Select("select school_admin_id from teacher where teacher_id = #{teacherId}")
    Integer selectSchoolIdAdminByTeacherId(@Param("teacherId") Long teacherId);

    List<Integer> getTeacherIdByAdminId(Integer adminId);

    @Select("select count(id) from teacher where school_admin_id=#{teacherId}")
    int getTeacherCountByAdminId(Long teacherId);

    @Update("update teacher set create_student_number=0")
    void updateCreateStudentNumber();
}
