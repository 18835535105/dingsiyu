package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.vo.SeniorityVo;
import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.common.pojo.Duration;
import com.zhidejiaoyu.common.pojo.DurationExample;
import com.zhidejiaoyu.common.pojo.Student;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SimpleDurationMapper extends BaseMapper<Duration> {
    int countByExample(DurationExample example);

    int deleteByExample(DurationExample example);

    int deleteByPrimaryKey(Long id);

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

    /**
     * 查询当前课程总有效学习时间
     *
     * @param stuId
     * @param courses
     * @return map key:课程id；value：有效时间（秒）
     */
    @MapKey("id")
    Map<Long, Map<String, BigDecimal>> countTotalValidTimeMapByCourseId(@Param("stuId") Long stuId, @Param("courses") List<Course> courses);

    Integer valid_timeIndex(@Param("student_id") Long student_id, @Param("courseId") Integer courseId, @Param("model") int model);

    /**
     * 查询学生每日的在线时长和有效时长
     *
     * @param student
     * @return map: key: validTime 有效时长，value：onlineTime 在线时长
     */
    @MapKey("validTime")
    List<Map<String, Object>> selectValidTimeAndOnlineTime(@Param("student") Student student);

    List<SeniorityVo> planSeniority(@Param("area") String area, @Param("school_name") String school_name, @Param("grade") String grade, @Param("squad") String squad, @Param("study_paragraph") String study_paragraph, @Param("haveTime") Integer haveTime, @Param("version") String version);

    @Select("select SUM(valid_time) AS valid_time FROM duration  WHERE student_id = #{stuId}  GROUP BY student_id")
    Integer onePlanSeniority(@Param("stuId") Long stuId);

    List<SeniorityVo> planSenioritySchool(@Param("area") String area, @Param("school_name") String school_name, @Param("study_paragraph") String study_paragraph, @Param("haveTime") Integer haveTime, @Param("version") String version);

    List<SeniorityVo> planSeniorityNationwide(@Param("study_paragraph") String study_paragraph, @Param("haveTime") Integer haveTime, @Param("version") String version);

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
}
