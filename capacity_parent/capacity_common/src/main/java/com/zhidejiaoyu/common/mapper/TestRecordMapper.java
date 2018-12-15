package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.Vo.SeniorityVo;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.pojo.TestRecordExample;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface TestRecordMapper extends BaseMapper<TestRecord> {
    int countByExample(TestRecordExample example);

    int deleteByExample(TestRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(TestRecord record);

    List<TestRecord> selectByExample(TestRecordExample example);

    TestRecord selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TestRecord record, @Param("example") TestRecordExample example);

    int updateByExample(@Param("record") TestRecord record, @Param("example") TestRecordExample example);

    int updateByPrimaryKeySelective(TestRecord record);

    int updateByPrimaryKey(TestRecord record);

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
     * 查询当前学生有无进行当前单元的测试记录
     *
     * @param studentId
     * @param unitId
     * @param genre      测试类型（学前游戏测试，等级测试，单元闯关测试，效果检测，阶段测试，复习测试，学后测试）
     * @param studyModel 学习模块
     * @return
     */
    TestRecord selectByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                          @Param("genre") String genre, @Param("studyModel") String studyModel);

    List<TestRecord> showRecord(@Param("student_id") Long student_id);

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
    @Select("select count(history_bad_point) from test_record where history_bad_point < 80 and student_id = #{student.id}")
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
     * @param classify 类型 0=单词图鉴 1=慧记忆 2=听写 3=默写 4=例句听力 5=例句翻译 6=例句默写
     * @return
     */
    Integer selectUnitTestMaxPointByStudyModel(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("model") Integer classify);

    List<SeniorityVo> planSeniority(@Param("grade") String grade, @Param("study_paragraph") String study_paragraph, @Param("haveTest") Integer haveTest, @Param("version") String version, @Param("classId")Long classId);
    
    @Select("SELECT COUNT(id) AS testCount FROM test_record WHERE student_id = #{stuId}  GROUP BY student_id")
    Integer onePlanSeniority(@Param("stuId") Long stuId);

    List<SeniorityVo> planSenioritySchool(@Param("study_paragraph") String study_paragraph, @Param("haveUnit") Integer haveUnit, @Param("version") String version, @Param("teacherId")Long teacherId);

    List<SeniorityVo> planSeniorityNationwide(@Param("study_paragraph") String study_paragraph,@Param("haveTest") Integer haveTest, @Param("version") String version);

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
     * @return  key 单元id
     */
    @MapKey("unitId")
    Map<Long, Map<Long, Long>> selectHasUnitTest(@Param("studentId") Long studentId, @Param("unitIds") List<Long> unitIds);
}