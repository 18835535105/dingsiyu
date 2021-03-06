package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.pojo.TestRecordInfo;
import com.zhidejiaoyu.common.vo.SeniorityVo;
import com.zhidejiaoyu.common.vo.testVo.TestDetailVo;
import com.zhidejiaoyu.common.vo.testVo.TestRecordVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface TestRecordMapper extends BaseMapper<TestRecord> {
    @Delete("delete from test_record where student_id = #{studentId}")
    int deleteByStudentId(Long studentId);

    /**
     * 查看当前单元已完成单元闯关测试的模块的个数,排除例句默写
     *
     * @param studentId
     * @param unitId
     * @param type      1=慧记忆 2=听写 3=默写 4=例句听力 5=例句翻译
     * @return
     */
    int selectByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") Integer type);

    /**
     * 查询当前学生有无进行当前单元的测试记录,如果有取better_count最大的一条记录
     *
     * @param studentId
     * @param unitId
     * @param genre      测试类型（学前游戏测试，等级测试，单元闯关测试，效果检测，阶段测试，复习测试，学后测试）
     * @param studyModel 学习模块
     * @return
     */
    TestRecord selectByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                          @Param("genre") String genre, @Param("studyModel") String studyModel);

    /**
     * 读取离线在最后一回的测试数据
     *
     * @param studentId
     * @param unitId
     * @param genre
     * @param studyModel
     * @return
     */
    TestRecord selectByStudentIdAndUnitIdAndGenreAndStudyModel(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                                               @Param("genre") String genre, @Param("studyModel") String studyModel);


    TestRecord selectByStudentIdAndUnitIdAndGenre(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                                  @Param("genre") String genre);

    /**
     * 查询指定类型的测试记录
     *
     * @param studentId
     * @param type      1:单词测试记录；2：句型测试记录
     * @return
     */
    List<TestRecordVo> showRecord(@Param("student_id") Long studentId, @Param("type") Integer type);

    /**
     * 查看学生今日单元闯关个数
     *
     * @param studentId
     * @param flag      条件标识	1：单元闯关成功个数；2：单元闯关个数
     * @return
     */
    int countUnitTest(@Param("studentId") Long studentId, @Param("flag") int flag);

    /**
     * 根据测试类型查询测试记录
     *
     * @param stuId
     * @param genre
     * @return
     */
    TestRecord selectByGenre(@Param("stuId") Long stuId, @Param("genre") String genre);

    /**
     * 根据测试类型查询测试记录数据
     *
     * @param stuId
     * @param genre
     * @return
     */
    List<TestRecord> selectListByGenre(@Param("stuId") Long stuId, @Param("genre") String genre);

    /**
     * 查询学生所有课程单元闯关成功的个数
     *
     * @param studentId
     * @return
     */
    @Select("select count(id) from test_record where student_id = #{studentId} and genre = '单元闯关测试' and point >=80")
    int countUnitTestSuccessByStudentId(@Param("studentId") Long studentId);

    Integer selectPoint(@Param("student_id") Long student_id, @Param("unit_id") Integer unit_id, @Param("genre") String genre, @Param("model") int model);

    /**
     * 获取学生已参与过的测试类型
     *
     * @param stuId
     * @return
     */
    @Select("select DISTINCT genre from test_record where student_id = #{stuId}")
    List<String> selectGenresByStudentId(@Param("stuId") Long stuId);

    @Select("select id from test_record where student_id = #{id} and genre = '学前游戏测试' LIMIT 0,1")
    Integer judgePreschoolTest(@Param("id") Long id);

    /**
     * 查看当天学生通过的测试中心测试个数
     *
     * @param student
     * @return
     */
    int selectTodayCompleteTestCenter(@Param("student") Student student);

    /**
     * 学生各个模块完成的单元数
     *
     * @param student
     * @return
     */
    @MapKey("studentId")
    Map<Long, Map<Long, Object>> countCompleteByStudentId(@Param("student") Student student);

    /**
     * 学生测试不及格总次数
     *
     * @param student
     * @return
     */
    @Select("select count(history_bad_point) from test_record where point < 80 and student_id = #{student.id}")
    Integer countTestFailByStudent(@Param("student") Student student);

    /**
     * 查看学生指定课程指定测试类型的最高分
     *
     * @param stuId
     * @param courseId
     * @param genre      测试类型
     * @param studyModel 测试模块
     * @return
     */
    List<TestRecord> selectMaxPointByStudyModel(@Param("stuId") long stuId, @Param("courseId") Long courseId, @Param("genre") String genre, @Param("studyModel") String studyModel);

    /**
     * 获取当前学生摸底测试次数
     *
     * @param student
     * @return
     */
    @Select("SELECT count(id) from test_record where student_id = #{student.id} and `explain` = '摸底测试'")
    Integer countLevelTestCountByStudentId(@Param("student") Student student);

    /**
     * 修改游戏测试记录次数为 2 次
     *
     * @param student
     */
    void updateGameRecord(@Param("student") Student student);

    @Select("SELECT id from test_record where genre = #{model} AND student_id = #{studentId} AND course_id = #{course_id} limit 0,1")
    Integer selectJudgeTest(@Param("course_id") String course_id, @Param("studentId") Long studentId, @Param("model") String model);

    Integer selectJudgeTestToModel(@Param("course_id") String course_id, @Param("student_id") Long student_id, @Param("model") int model, @Param("select") int select);

    /**
     * 当前单元单元闯关测试历史最高成绩
     *
     * @param studentId
     * @param unitId
     * @param classify  类型 0=单词图鉴 1=慧记忆 2=听写 3=默写 4=例句听力 5=例句翻译 6=例句默写
     * @return
     */
    Integer selectUnitTestMaxPointByStudyModel(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("model") Integer classify);

    List<SeniorityVo> planSeniority(@Param("grade") String grade, @Param("study_paragraph") String study_paragraph, @Param("haveTest") Integer haveTest, @Param("version") String version, @Param("classId") Long classId);

    @Select("SELECT COUNT(id) AS testCount FROM test_record WHERE student_id = #{stuId}  GROUP BY student_id")
    Integer onePlanSeniority(@Param("stuId") Long stuId);

    List<SeniorityVo> planSenioritySchool(@Param("study_paragraph") String study_paragraph, @Param("haveUnit") Integer haveUnit, @Param("version") String version, @Param("teacherId") Long teacherId);

    List<SeniorityVo> planSeniorityNationwide(@Param("study_paragraph") String study_paragraph, @Param("haveTest") Integer haveTest, @Param("version") String version);

    /**
     * 查询学生不同课程课程测试的最低分
     *
     * @return
     */
    @Select("select min(point) from test_record where genre = '课程测试' group by course_id")
    List<Integer> selectCourseTestMinPoint();

    /**
     * 学生指定单元的战胜率
     *
     * @param studentId
     * @param unitId
     * @return
     */
    Double selectVictoryRate(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    /**
     * 获取学生已经进行过单元闯关测试的例句单元
     *
     * @param studentId
     * @param unitIds
     * @return key 单元id
     */
    @MapKey("unitId")
    Map<Long, Map<Long, Long>> selectHasUnitTest(@Param("studentId") Long studentId, @Param("unitIds") List<Long> unitIds);

    Integer selectMaxPointByUnitStudentModel(Map<String, Object> selMap);

    /**
     * 获取学生测试记录详情头部信息
     *
     * @param stuId
     * @param testId
     * @param type   1:单词；2：句型；3：课文
     * @return
     */
    TestDetailVo selectTestDetailVo(@Param("stuId") Long stuId, @Param("testId") Long testId, @Param("type") Integer type);

    /**
     * 获取学生测试记录详情表格内容
     *
     * @param testId
     * @return
     */
    List<TestRecordInfo> selectTestRecordInfo(Long testId);

    /**
     * 统计当前学生学前游戏测试次数
     *
     * @param stu
     * @return
     */
    @Select("select count(id) from test_record where student_id = #{stu.id} and genre = '学前游戏测试'")
    int countGameCount(@Param("stu") Student stu);


    TestRecord selectByStudentIdAndGenre(@Param("id") Long id, @Param("unitId") Long unitId);

    /**
     * 查询当前测试是否已存在
     *
     * @param studentId
     * @param unitId
     * @param genre
     * @param studyModel
     * @param testStartTime
     * @return
     */
    int countCurrentTestByStudentId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("genre") String genre,
                                    @Param("studyModel") String studyModel, @Param("testStartTime") Date testStartTime);

    /**
     * 删除学生的学前游戏测试记录
     *
     * @param studentId
     */
    @Delete("delete from test_record where student_id = #{studentId} and genre = '学前游戏测试'")
    void deleteGameRecord(@Param("studentId") Long studentId);

    Integer selectUnitTestMaxPointByStudyModels(@Param("studentId") Long studentId, @Param("letterUnitId") Integer letterUnitIds, @Param("model") Integer model);

    /**
     * 统计学生今天进行的单元闯关测试次数
     *
     * @param studentId
     * @return
     */
    @Select("select count(id) from test_record where student_id = #{studentId} and genre = '单元闯关测试' and TO_DAYS(test_start_time) = TO_DAYS(now())")
    int countTodayTestUnitCount(@Param("studentId") Long studentId);

    Integer selectReadCountByCourseId(@Param("courseId") long courseId);

    int selCount(@Param("studentId") Long studenId, @Param("courseId") Long courseId, @Param("unitId") Long unitId, @Param("studyModel") String studyModel, @Param("genre") String genre, @Param("group") Integer group);

    /**
     * 查询学生获得 100 分的测试记录
     *
     * @param studentId
     * @return
     */
    List<TestRecord> selectFullPoint(@Param("studentId") Long studentId);

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIdList);

    TestRecord selectByStudentIdAndGenreAndStudyModel(@Param("studentId") Long studentId, @Param("genre") String genre, @Param("studyModel") String studyModel);

    /**
     * 统计指定测试类型在指定日期范围的测试次数
     *
     * @param studentId
     * @param genre
     * @param beginTime
     * @param endTime
     * @return
     */
    @Select("select count(id) from test_record where student_id = #{studentId} and genre = #{genre} and test_start_time >= #{beginTime} and test_end_time <= #{endTime}")
    int countByGenreWithBeginTimeAndEndTime(@Param("studentId") Long studentId, @Param("genre") String genre,
                                            @Param("beginTime") String beginTime, @Param("endTime") String endTime);

    /**
     * 查询学生若干次测试的平均分
     *
     * @param studentId
     * @param limit
     * @return
     */
    Double selectScoreAvg(@Param("studentId") Long studentId, @Param("limit") int limit);

    /**
     * 查询学生指定日期内的平均分
     *
     * @param studentId
     * @param startTime
     * @param endTime
     * @return
     */
    Double selectScoreAvgByStartDateAndEndDate(@Param("studentId") Long studentId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    Integer selectFractionByStudentId(@Param("studentId") long studentId);

    @MapKey("studentId")
    Map<Long, Map<String, Object>> selectByStudentIdsAndGenreAndDate(@Param("studentIds") List<Long> studentIds,
                                                                     @Param("genre") String smallappGenre, @Param("date") Date beforeDaysDate);

    @MapKey("studentId")
    Map<Long, Map<String, Object>> selectByGenreAndDate(@Param("genre") String genre, @Param("date") Date date);

    /**
     * 获取各个课程下已经参与过金币试卷测试的单元数
     *
     * @param studentId
     * @param courseIds
     * @return
     */
    @MapKey("courseId")
    Map<Long, Map<Long, Object>> countGoldTestByStudentIdAndCourseIds(@Param("studentId") Long studentId, @Param("courseIds") List<Long> courseIds);

    /**
     * 查询当前模块当天是否已经测试
     *
     * @param studentId
     * @param genre
     * @param date
     * @return
     */
    Integer selectByStudentIdAndGenreAndEndTime(@Param("studentId") Long studentId, @Param("genre") String genre, @Param("date") Date date);

    Integer selectFractionByStudentIdAndDate(@Param("studentId") Long studentId, @Param("date") String date);

    /**
     * 统计学生指定日期内成绩总和
     *
     * @param studentId
     * @param beginDate
     * @param endDate
     * @return
     */
    Integer selectTotalPointByBeginDateAndEndDate(@Param("studentId") Long studentId, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    /**
     * 查看学生指定日期内金币测试最高分
     *
     * @param studentId
     * @param beginDate
     * @param endDate
     * @return
     */
    Integer selectGoldTestMaxPointByBeginDateAndEndDate(@Param("studentId") Long studentId, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    /**
     * 统计学生总测试题个数
     *
     * @param studentId
     * @return
     */
    @Select("select sum(quantity) from test_record where student_id = #{studentId}")
    Integer countTotalSubjects(@Param("studentId") Long studentId);

    /**
     * 统计学生今天金币测试次数
     *
     * @param studentId
     * @return
     */
    @Select("select count(id) from test_record where student_id = #{studentId} and genre like '金币%' and TO_DAYS(test_start_time) = TO_DAYS(now())")
    int countGoldTestByStudentIdToday(@Param("studentId") Long studentId);

    /**
     * 查询指定单元是否已经进行过相关测试
     *
     * @param unitIds
     * @param studentId
     * @param genre
     * @return
     */
    List<TestRecord> selectListByUnitIdsAndGenre(@Param("unitIds") List<Long> unitIds, @Param("studentId") Long studentId, @Param("genre") String genre);

    /**
     * 统计指定类型的测试学生测试次数
     *
     * @param genre
     * @param studentId
     * @return
     */
    Integer countByGenreAndStudentId(@Param("genre") String genre, @Param("studentId") Long studentId);
}

