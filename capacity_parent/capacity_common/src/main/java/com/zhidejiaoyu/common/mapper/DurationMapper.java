package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.Vo.SeniorityVo;
import com.zhidejiaoyu.common.pojo.*;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DurationMapper {
    int countByExample(DurationExample example);

    int deleteByExample(DurationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Duration record);

    int insertSelective(Duration record);

    List<Duration> selectByExample(DurationExample example);

    Duration selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Duration record, @Param("example") DurationExample example);

    int updateByExample(@Param("record") Duration record, @Param("example") DurationExample example);

    int updateByPrimaryKeySelective(Duration record);

    int updateByPrimaryKey(Duration record);

    /**
     * 根据学生id删除
     *
     * @param studentId
     * @return
     */
    @Delete("delete from duration where student_id = #{studentId}")
    Integer deleteByStudentId(Long studentId);

    /**
     * 查找学生的总有效时长
     *
     * @param stuId
     * @return
     */
    @Select("select sum(valid_time) from duration where student_id = #{stuId}")
    Long countTotalValidTime(@Param("stuId") Long stuId);

    /**
     * 查询学生的总学习有效时长 （精确到秒）
     *
     * @param stuId
     * @return
     */
    @Select("select sum(valid_time) from duration where student_id = #{stuId}")
    Long selectValidTimeByStudentId(@Param("stuId") Long stuId);

    @Select("select SUM(valid_time) AS valid_time from duration where date_format(login_time, '%Y-%m-%d')>=#{weekStart} and date_format(login_time, '%Y-%m-%d')<=#{weekEnd} AND student_id = #{studentId}")
    Integer totalTime(@Param("weekStart") String weekStart, @Param("weekEnd") String weekEnd, @Param("studentId") Long studentId);

    /**
     * 获取学生总在线时长(秒)
     *
     * @param stuId
     * @return
     */
    @Select("SELECT SUM(online_time) FROM duration where student_id = #{stuId}")
    Integer selectTotalOnlineByStudentId(@Param("stuId") Long stuId);

    Integer selectValidTime(@Param("studentId") Long studentId, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /**
     * 今天在线数据
     *
     * @param studentId
     * @param beginTime
     * @param endTime
     * @return
     */
    Integer selectOnlineTime(@Param("studentId") Long studentId, @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /**
     * 查询当前课程总有效学习时间
     *
     * @param stuId
     * @param courses
     * @return map key:课程id；value：有效时间（秒）
     */
    @MapKey("id")
    Map<Long, Map<String, BigDecimal>> countTotalValidTimeMapByCourseId(@Param("stuId") Long stuId, @Param("courses") List<Course> courses);

    Integer valid_timeIndex(@Param("student_id") Long student_id, @Param("unit_id") Integer unit_id, @Param("model") int model);

    /**
     * 查询学生当日的在线时长和有效时长
     *
     * @param student
     * @return map: key: validTime 有效时长，value：onlineTime 在线时长
     */
    @MapKey("validTime")
    List<Map<String, Object>> selectValidTimeAndOnlineTime(@Param("student") Student student);

    /**
     * 获取学生总在线时长
     *
     * @param student
     * @return
     */
    @Select("select sum(online_time) from duration where student_id = #{student.id}")
    Long countTotalOnlineTime(@Param("student") Student student);

    List<SeniorityVo> planSeniority(@Param("grade") String grade,  @Param("study_paragraph") String study_paragraph, @Param("haveUnit") Integer haveUnit, @Param("version") String version, @Param("classId")Long classId);

    @Select("select SUM(valid_time) AS valid_time FROM duration  WHERE student_id = #{stuId}  GROUP BY student_id")
    Integer onePlanSeniority(@Param("stuId") Long stuId);

    List<SeniorityVo> planSenioritySchool(@Param("study_paragraph") String study_paragraph, @Param("haveUnit") Integer haveUnit, @Param("version") String version, @Param("teacherId")Long teacherId);

    List<SeniorityVo> planSeniorityNationwide(@Param("study_paragraph") String study_paragraph,@Param("haveTime") Integer haveTime, @Param("version") String version);

    /**
     * 学生有效时间,已小时为单位
     * @param studentId
     * @return 小时
     */
    @Select("select IFNULL(round((sum(valid_time)/60)),0) from duration where student_id = #{studentId}")
    int labelValidTimeByStudentId(@Param("studentId") Long studentId);

    /**
     * 获取学生前7个工作日的学习效率
     *
     * @param studentWorkDay
     * @return
     */
    Double selectStudyEfficiency(@Param("studentWorkDay") StudentWorkDay studentWorkDay);

    /**
     * 查询学生上次的登录时间和退出时间
     *
     * @param studentId
     * @return
     */
    Duration selectLastLoginDuration(@Param("studentId") Long studentId);

    /**
     * 查看学生当前登录记录的在线时长个数
     *
     * @param student
     * @param loginTime
     * @return
     */
    int countOnlineTimeWithLoginTime(@Param("student") Student student, @Param("loginTime") Date loginTime);

    /**
     * 统计今日学生当前模块下当前单元的总有效时长
     *
     * @param stuId
     * @param model  学习模块
     * @param unitId
     * @return  有效时长，单位：秒
     */
    @Select("select sum(valid_time) from duration where to_days(now()) = to_days(login_time) and student_id = #{stuId} and study_model = #{model} and unit_id = #{unitId}")
    Long sumTodayModelValidTime(@Param("stuId") Long stuId, @Param("model") int model, @Param("unitId") Long unitId);

    /**
     * 查询当前课程、单元下指定模块的有效时长
     *
     * @param studentId
     * @param courseId
     * @param unitId
     * @param loginTime
     * @param key   模块id
     * @return
     */
    List<Duration> selectByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("unitId") Long unitId, @Param("loginTime") String loginTime, @Param("key") Integer key);
}