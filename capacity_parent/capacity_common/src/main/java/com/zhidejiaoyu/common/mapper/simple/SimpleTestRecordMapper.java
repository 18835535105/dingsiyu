package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.vo.SeniorityVo;
import com.zhidejiaoyu.common.vo.simple.testVo.TestRecordVo;
import com.zhidejiaoyu.common.vo.simple.testVo.TestDetailVo;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.TestRecord;
import com.zhidejiaoyu.common.pojo.TestRecordExample;
import com.zhidejiaoyu.common.pojo.TestRecordInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface SimpleTestRecordMapper extends BaseMapper<TestRecord> {
	List<TestRecord> selectByExample(TestRecordExample example);

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
	 * @param type
	 *            1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写
	 * @return
	 */
	int selectByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") Integer type);

	/**
	 * 查询当前学生有无进行当前单元的测试记录
	 *
	 * @param studentId
	 * @param unitId
	 * @param genre
	 *            测试类型（学前游戏测试，等级测试，单元闯关测试，效果检测，阶段测试，复习测试，学后测试）
	 * @param studyModel
	 *            学习模块
	 * @return
	 */
	TestRecord selectByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId,
                                          @Param("genre") String genre, @Param("studyModel") String studyModel);

	List<TestRecordVo> showRecord(@Param("student_id") Long student_id);

	/**
	 * 查看学生今日单元闯关个数
	 *
	 * @param studentId
	 * @param flag
	 *            条件标识 1：单元闯关成功个数；2：单元闯关个数
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
	@Select("select count(id) from test_record where student_id = #{studentId} and genre = '单元闯关测试' and pass = 1")
	int countUnitTestSuccessByStudentId(@Param("studentId") Long studentId);

	Integer selectPoint(@Param("student_id") Long student_id, @Param("unit_id") Integer unit_id,
                        @Param("genre") String genre, @Param("model") int model);

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
	@Select("select count(id) from test_record where student_id = #{student.id} and pass = 2")
	Integer countTestFailByStudent(@Param("student") Student student);

	/**
	 * 查看学生指定课程指定测试类型的最高分
	 *
	 * @param stuId
	 * @param courseId
	 * @param genre
	 *            测试类型
	 * @param studyModel
	 *            测试模块
	 * @return
	 */
	List<TestRecord> selectMaxPointByStudyModel(@Param("stuId") long stuId, @Param("courseId") Long courseId,
                                                @Param("genre") String genre, @Param("studyModel") String studyModel);

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
	Integer selectJudgeTest(@Param("course_id") String course_id, @Param("studentId") Long studentId,
                            @Param("model") String model);

	Integer selectJudgeTestToModel(@Param("course_id") String course_id, @Param("student_id") Long student_id,
                                   @Param("model") int model, @Param("select") int select);

	List<SeniorityVo> planSeniority(@Param("area") String area, @Param("school_name") String school_name,
                                    @Param("grade") String grade, @Param("squad") String squad,
                                    @Param("study_paragraph") String study_paragraph, @Param("haveTest") Integer haveTest,
                                    @Param("version") String version);

	@Select("SELECT COUNT(id) AS testCount FROM test_record WHERE student_id = #{stuId}  GROUP BY student_id")
	Integer onePlanSeniority(@Param("stuId") Long stuId);

	List<SeniorityVo> planSenioritySchool(@Param("area") String area, @Param("school_name") String school_name,
                                          @Param("study_paragraph") String study_paragraph, @Param("haveTest") Integer haveTest,
                                          @Param("version") String version);

	List<SeniorityVo> planSeniorityNationwide(@Param("study_paragraph") String study_paragraph,
                                              @Param("haveTest") Integer haveTest, @Param("version") String version);

	/**
	 * 判断学生"某模块,某测试类型"是否存在测试记录
	 *
	 * @param studentId
	 * @param courseId
	 * @param unitId
	 * @param type
	 *            1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写;
	 *            9:词组默写;
	 * @param testType 测试类型
	 * @return id
	 */
	Long getWhetherTest(@Param("studentId") Long studentId, @Param("courseId") Long courseId,
                          @Param("unitId") Long unitId, @Param("type") int type, @Param("testType") String testType);

	/**
	 * 查询学生不同课程课程测试的最低分
	 *
	 * @return
	 */
	@Select("select min(point) from test_record where genre = '课程测试' group by course_id")
	List<Integer> selectCourseTestMinPoint();

	@Select("select id from test_record where student_id = #{studentId} and course_id = #{courseId} and genre = #{genre} and study_model = #{typeStr} limit 1")
	Long getAfterClassTestInfo(@Param("studentId") long studentId, @Param("courseId") long courseId, @Param("typeStr") String typeStr, @Param("genre") String genre);

	@Delete("delete from test_record where student_id = #{studentId} and unit_id = #{unitId}")
	void deleteByStudenIdByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

	@MapKey("unit_id")
	Map<Long, Map<Long, Object>> getUnitTestByCourseId(@Param("courseId") long courseId, @Param("studentId") long studentId);

	@Select("select max(point) from test_record where unit_id = #{unitId}  and student_id = #{studentId} AND study_model = #{modelName}")
	Integer selectUnitTestMaxPointByStudyModel(@Param("studentId") Long studentId, @Param("unitId") Object unitId, @Param("modelName") String modelName);

	@Select("select id from test_record where course_id = #{courseId} and student_id = #{studentId} and genre = '学后测试' limit 1")
	Long getXHTest(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

	/**
	 * 获取学生测试记录详情头部信息
     *
	 * @param stuId
	 * @param testId
	 * @return
	 */
    TestDetailVo selectTestDetailVo(@Param("stuId") Long stuId, @Param("testId") Long testId);

    /**
     * 获取学生测试记录详情表格内容
     *
     * @param testId
     * @return
     */
    List<TestRecordInfo> selectTestRecordInfo(Long testId);

	/**
	 * 更新测试记录为之前测试状态
	 *
	 * @param studentId
	 * @param unitId
	 */
	@Update("update test_record set type = 2 where student_id = #{studentId} and unit_id = #{unitId}")
	void updateByStudentAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

	/**
	 * 获取单元闯关历史最高分
	 *
	 * @param studentId
	 * @param unitIds
	 * @param modelName
	 * @return
	 */
	@MapKey("unitId")
	Map<Long, Map<Long, Integer>> selectUnitTestMaxPointMapByUnitIds(@Param("studentId") Long studentId, @Param("unitIds") List<Long> unitIds, @Param("modelName") String modelName);

	/**
	 * 查询学生上次测试成绩得分
	 *
	 * @param studentId
	 * @return
	 */
    Integer selectPrePoint(@Param("studentId") Long studentId);

	/**
	 * 统计学生今天进行的单元闯关测试次数
	 *
	 * @param studentId
	 * @return
	 */
	@Select("select count(id) from test_record where student_id = #{studentId} and genre = '单元闯关测试' and TO_DAYS(test_start_time) = TO_DAYS(now())")
	int countTodayTestUnitCount(@Param("studentId") Long studentId);
}
