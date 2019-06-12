package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Course;
import com.zhidejiaoyu.common.pojo.CourseExample;
import com.zhidejiaoyu.common.pojo.Unit;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface SimpleCourseMapper extends BaseMapper<Course> {
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
     * 查询学生的所有课程
     *
     * @param stuId
     * @return
     */
    List<Course> selectByStudentId(@Param("stuId") Long stuId);

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
	Integer countUnitAnnotation(@Param("id") Long id);

	Map<String, Object> postStudentByCourse(@Param("courseId") Integer courseId);

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
	Integer showSelectWord(@Param("id") int id);
    /**
     * 查看这个课程下是否存在例句
     *
     * @param id 课程id
     * @return
     */
    @Select("select a.id FROM unit a JOIN unit_sentence b ON a.id = b.unit_id JOIN sentence c ON b.sentence_id = c.id AND a.course_id = #{id} LIMIT 0,1 ")
	Integer showSelectSentence(@Param("id") int id);

	Map<String, Object> selectCourseVersion(@Param("id") Long id);

	/**
	 * 根据id获取课程拼接名
	 *
	 * @param course_id
	 * @return
	 */
	@Select("select course_name from course where id = #{id}")
	String selectByCourseName(@Param("id") String course_id);

    /**
     * 查询课程下所有单元信息及单元内单词数量
     *
     * @param courseId 课程id
     * @return
     */
    @Select("SELECT count(uv.vocabulary_id) wordCount, u.unit_name unitName, u.id unitId FROM unit u, unit_vocabulary uv, vocabulary v WHERE u.id = uv.unit_id AND v.id =uv.vocabulary_id AND v.delStatus = 1 AND u.course_id = #{courseId} GROUP BY u.id")
    List<Map<String, Object>> getAllUnitInfos(@Param("courseId") Long courseId);

	List<Map> getSimpleCourseByStudentIdByType(@Param("studentId") long studentId, @Param("type") String type);

	@Select("select id from course where id = #{courseId} AND version like '%${end}'")
	Long getApologySoundModelBycourseId(Long courseId, String end);

    /**
     * 获取学生所有课程
     *
     * @param studentId
     * @return
     */
    List<String> getStudentCourseAllByStudentId(@Param("studentId") long studentId);

    /**
     * 获取学生指定模块下的所有课程
     *
     * @param stuId
     * @param typeStr   模块名
     * @return key 课程id
     * value 课程名称
     */
    List<Map<String, Object>> selectAllCourseByStuIdAndType(@Param("stuId") Long stuId, @Param("typeStr") String typeStr);

    /**
     * 课程下每个单元单词总数
     *
     * @param courseId 课程id
     * @return
     */
    @MapKey("id")
	Map<Long, Map<Long, Object>> unitsWordSum(@Param("courseId") long courseId);

    List<Map<String,Object>> getCourseByIds(@Param("courseIds") List<Long> courseIds);
}
