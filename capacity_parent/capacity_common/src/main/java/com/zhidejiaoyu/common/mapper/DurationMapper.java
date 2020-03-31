package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.vo.SeniorityVo;
import com.zhidejiaoyu.common.vo.smallapp.studyinfo.DurationInfoVO;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolDetail;
import com.zhidejiaoyu.common.pojo.*;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface DurationMapper extends BaseMapper<Duration> {

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

    /**
     * 获取学生指定时间段的有效时长（单位：秒）
     *
     * @param studentId
     * @param beginTime 起始时间
     * @param endTime   结束时间
     * @return
     */
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

    List<SeniorityVo> planSeniority(@Param("grade") String grade, @Param("study_paragraph") String study_paragraph, @Param("haveUnit") Integer haveUnit, @Param("version") String version, @Param("classId") Long classId);

    @Select("select SUM(valid_time) AS valid_time FROM duration  WHERE student_id = #{stuId}  GROUP BY student_id")
    Integer onePlanSeniority(@Param("stuId") Long stuId);

    List<SeniorityVo> planSenioritySchool(@Param("study_paragraph") String study_paragraph, @Param("haveUnit") Integer haveUnit, @Param("version") String version, @Param("teacherId") Long teacherId);

    List<SeniorityVo> planSeniorityNationwide(@Param("study_paragraph") String study_paragraph, @Param("haveTime") Integer haveTime, @Param("version") String version);

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
     * 查询当前课程、单元下指定模块的有效时长
     *
     * @param studentId
     * @param courseId
     * @param unitId
     * @param loginTime
     * @param key       模块id
     * @return
     */
    List<Duration> selectByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("unitId") Long unitId, @Param("loginTime") String loginTime, @Param("key") Integer key);

    /**
     * 查询当前 loginOutTime 为当前时间的记录条数
     *
     * @param studentId
     * @param loginOutTime
     * @return
     */
    int countByLoginOutTime(@Param("studentId") Long studentId, @Param("loginOutTime") String loginOutTime);

    /**
     * 查询学生当天最后保存有效时长的时间（不包含退出时的时间）
     *
     * @param studentId
     * @return
     */
    Duration selectLastDuration(@Param("studentId") Long studentId);

    /**
     * 计算本次学习结束距离上次学习结束的在线时长信息
     *
     * @param studentId
     * @return
     */
    Duration countOnlineTimeWithLoginOutTine(@Param("studentId") Long studentId);

    /**
     * 查询校区下指定日期学生登录信息及在线时长
     *
     * @param date
     * @return
     */
    List<StudentInfoSchoolDetail> selectExportStudentOnlineTimeWithSchoolDetail(String date);

    Date selectLoginTimeByDate(@Param("studentId") Long studentId, @Param("date") Date date);

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIdList);

    /**
     * 查询学生的学习日期及在线时长
     *
     * @param studentId
     * @return
     */
    List<DurationInfoVO> selectLearnDateAndOnlineTime(@Param("studentId") Long studentId);

    /**
     * 查询指定日期的学习时长及学习模块
     *
     * @param studentId
     * @param learnDateList
     * @return
     */
    List<DurationInfoVO> selectDurationInfos(@Param("studentId") Long studentId, @Param("learnDateList") List<String> learnDateList);

    /**
     * 查询学生最后一次的学习效率
     *
     * @param studentId
     * @return
     */
    Double selectLastStudyEfficiency(@Param("studentId") Long studentId);

    List<Integer> selectDayTimeByStudentId(@Param("studentId") long studentId);

    @MapKey("studentId")
    Map<String,Map<String,Object>> selectValidTimeByStudentIds(@Param("studentIds") List<Long> studentIds,@Param("date") Date date);

    Date selectLoginTimeByStudentIdAndDate(@Param("studentId") Long studentId,@Param("date") Date date);
}
