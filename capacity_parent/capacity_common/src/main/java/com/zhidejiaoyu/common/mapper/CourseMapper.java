package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.common.pojo.CourseExample;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Unit;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CourseMapper extends BaseMapper<Course> {
    int countByExample(CourseExample example);

    int deleteByExample(CourseExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(Course record);

    List<Course> selectByExample(CourseExample example);

    Course selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Course record, @Param("example") CourseExample example);

    int updateByExample(@Param("record") Course record, @Param("example") CourseExample example);

    int updateByPrimaryKeySelective(Course record);

    int updateByPrimaryKey(Course record);

    List<Course> page(Course cou);

    Integer countUnit(Course long1);

    int addCourse(Course course);

    String selectCourseName(int id);

    /**
     * 新增单元
     *
     * @param unitIndex
     */
    @Insert("insert into unit(course_id, unit_name, joint_name,unit_index) values(#{state}, #{str}, #{string},#{unitIndex})")
    int addUnit(@Param("state") int state, @Param("str") String str, @Param("string") String string, @Param("unitIndex") int unitIndex);

    /**
     * 根据课程名查询课程id
     */
    @Select("select id from course where version = #{version} and grade = #{grade} and label = #{label} and delStatus = 1")
    List<Integer> selectCourse(@Param("version") String version, @Param("grade") String grade,
                               @Param("label") String label);

    @Select("select id from unit where joint_name = #{name} and delStatus = 1")
    Integer selectUnit(@Param("name") String string);

    /**
     * 根据课程id查询单元名
     */
    List<Unit> selectListCourseName(int id);

    /**
     * 删除课程
     */
    @Update("update course set delStatus = 2 where id = #{id}")
    int delCourse(int id);

    /**
     * 删除单元
     */
    @Update("update unit set delStatus = 2 where course_id = #{id}")
    void delUnit(int id);

    /**
     * 根据课程单元名查询单元id
     */
    @Select("select id from unit where joint_name = #{jointName}")
    Integer unitNametoJointName(@Param("jointName") String jointName);

    @Update("update unit set joint_name = #{str} where course_id = #{id}")
    void editUnitToJointName(@Param("str") String str, @Param("id") int id);

    @Select("select b.unit_id from unit a INNER JOIN unit_vocabulary b on a.id = b.unit_id and a.course_id = #{id}")
    Integer selectUnit_id(@Param("id") Long id);

    // select id, course_name from course
    List<Course> showcoursename();

    List<Map<String, Object>> showWordName(int id);

    /**
     * 去重获取所有教材版本
     *
     * @return
     */
    @Select("select distinct(c.version) from course c where c.`status` = 1 and c.delStatus = 1")
    List<String> getVersions();

    /**
     * 去重获取所有学段
     *
     * @return
     */
    @Select("select distinct(c.study_paragraph) from course c where c.`status` = 1 and c.delStatus = 1")
    List<String> getStudyParagraph();

    /**
     * 去重获取所有年级
     *
     * @return
     */
    @Select("select distinct(c.grade) from course c where c.`status` = 1 and c.delStatus = 1")
    List<String> getGrades();

    /**
     * 去重获取所有标签
     *
     * @param label 标签名
     * @return
     */
    @Select("select distinct(c.label) from course c where c.`status` = 1 and c.delStatus = 1")
    List<String> getLabels();

    /**
     * 根据学生id查询可以看到的年级
     *
     * @param id
     * @return
     */
    @Select("select b.grade from student_unit a JOIN course b ON a.course_id = b.id AND a.student_id = #{id} GROUP BY b.grade")
    List<String> chooseGrade(@Param("id") Long id);

    /**
     * 根据学习id和年级查询标签
     *
     * @param id
     * @param grade
     * @return
     */
    List<Map<String, Object>> chooseGradeToLabel(Course cou);

    /**
     * 获取年级对应的所有教材版本
     *
     * @param gradeName
     * @return
     */
    @Select("select distinct(version) from course where grade like #{gradeName} and delStatus=1 and `status` = 1")
    List<String> getCourseVersonByGrade(String gradeName);

    /**
     * 根据课程的 ids 查询课程集合
     *
     * @param ids 多个课程id	1,2,3,4,5...
     * @return
     */
    List<Course> selectCourseByIds(@Param("ids") List<Long> ids);

    @Select("select grade from course where delStatus = 1 and grade <> '六年级' GROUP BY grade ORDER BY id")
    List<Course> retGrade();

    @Select("select version from course where grade = #{grade} and delStatus = 1 and `status` = 1 GROUP BY version")
    List<Course> retVersion(@Param("grade") String grade);

    @Select("select id, label, course_name as courseName from course where grade = #{grade} and version = #{version} and delStatus = 1 and `status` = 1")
    List<Course> retLabel(@Param("grade") String grade, @Param("version") String version);

    @Update("update study_count a SET a.count=a.count+1 where a.course_id = #{course_id} and a.student_id = #{id}")
    void updateStudy_count(@Param("course_id") String course_id, @Param("id") Long id);

    @Update("update learn set status = 0 where course_id = #{course_id} and student_id = #{id}")
    void updateLearn(@Param("course_id") String course_id, @Param("id") Long id);

    /**
     * 根据单元id查询课程id
     *
     * @param unitId
     * @return
     */
    @Select("select course_id from unit where id = #{unitId}")
    Long selectIdByUnitId(Long unitId);

    /**
     * 获取当前课程下的所有单词个数
     *
     * @param courses 当前课程信息
     * @return Map key:当前课程id；value：当前课程下的单词个数
     */
    @MapKey("id")
    Map<Long, Map<String, Long>> selectWordCountByCourseId(@Param("list") List<Course> courses);

    /**
     * 获取当前课程下的所有例句个数
     *
     * @param courses 当前课程信息
     * @return Map key:当前课程id；value：当前课程下的例句个数
     */
    @MapKey("id")
    Map<Long, Map<String, Long>> selectSentenceCountByCourseId(@Param("list") List<Course> courses);

    /**
     * 根据课程id集合获取课程名
     *
     * @param ids
     * @return
     */
    @MapKey("id")
    Map<Long, Map<Long, String>> selectCourseNameByIds(@Param("ids") List<Long> ids);

    /**
     * 根据单元id查询课程名称
     *
     * @param unitId
     * @return
     */
    @Select("select course_name from course where id = (select course_id from unit where id = #{unitId})")
    String selectCourseNameByUnitId(@Param("unitId") Long unitId);

	List<Map<String, Object>> courseLearnInfo(Long studentId);

	@Select("select count(*) from unit where course_id = #{id}")
	Integer countUnitAnnotation(@Param("id")Long id);

	Map<String, Object> postStudentByCourse(@Param("courseId")Integer courseId);
	
	/**
     * 去重获取所有教材版本
     *
     * @return
     */
    @Select("select distinct(c.version) from course c where c.delStatus = 1")
    List<String> getVersionsStatus();
    /**
     * 去重获取所有学段
     *
     * @return
     */
    @Select("select distinct(c.study_paragraph) from course c where c.delStatus = 1")
    List<String> getStudyParagraphStatus();
    /**
     * 去重获取所有年级
     *
     * @return
     */
    @Select("select distinct(c.grade) from course c where c.delStatus = 1")
	List<String> getGradesStatus();
    /**
     * 去重获取所有标签
     *
     * @param label 标签名
     * @return
     */
    @Select("select distinct(c.label) from course c where c.delStatus = 1")
	List<String> getLabelsStatus();

    /**
     * 查看这个课程下是否存在单词
     *
     * @param id 课程id
     * @return
     */
    @Select("select a.id FROM unit a JOIN unit_vocabulary b ON a.id = b.unit_id JOIN vocabulary c ON b.vocabulary_id = c.id AND c.delStatus = 1 AND a.course_id = #{id} LIMIT 0,1")
	Integer showSelectWord(@Param("id")int id);
    /**
     * 查看这个课程下是否存在例句
     *
     * @param id 课程id
     * @return
     */
    @Select("select a.id FROM unit a JOIN unit_sentence b ON a.id = b.unit_id JOIN sentence c ON b.sentence_id = c.id AND a.course_id = #{id} LIMIT 0,1 ")
	Integer showSelectSentence(@Param("id")int id);

	Map<String, Object> selectCourseVersion(@Param("id")Long id);

	/**
	 * 根据id获取课程拼接名
	 *
	 * @param course_id
	 * @return
	 */
	@Select("select course_name from course where id = #{id}")
	String selectByCourseName(@Param("id")String course_id);

    /**
     * 查询课程下所有单元信息及单元内单词数量
     *
     * @param courseId 课程id
     * @param type
     * @return
     */
    List<Map<String, Object>> getAllUnitInfos(@Param("courseId") Long courseId, @Param("type") Integer type);

    Map<String, Object> getCourseByCreamVersionName(String creamVersion);

    /**
     * 获取学生所有课程及其所有单词数量
     *
     * @param student
     * @return
     */
    List<Map<String, Object>> getAllCourseInfoWithWord(@Param("student") Student student);

    /**
     * 获取学生所有课程及其所有例句数量
     *
     * @param student
     * @return
     */
    List<Map<String, Object>> getAllCourseInfoWithSentence(@Param("student") Student student);

    /**
     * 查询学生句型所有课程的id和名称
     *
     * @param studentId
     * @return
     */
    List<Map<String, Object>> selectSentenceCourseIdAndCourseNameByStudentId(@Param("studentId") Long studentId);

    /**
     * 查询学生课文所有课程id和名称
     *
     * @param studentId
     * @return
     */
    List<Map<String, Object>> selectTextCourseIdAndCourseNameByStudentId(Long studentId);

    /**
     * 获取学生
     * @param student
     * @return
     */
    List<Map<String, Object>> selectVersionByStudent(@Param("student") Student student);

    /**
     * 获取学生当前版本下所有课程信息 id，courseName
     *
     * @param student
     * @param versionName
     * @return
     */
    List<Map<String, Object>> selectCourseByVersion(@Param("student") Student student, @Param("versionName") String versionName);

    List<Map<String,Object>> getAllVersion(Long studnetId);

    Map<String,Object> selectCourseByUnitId(Long unitId);
}