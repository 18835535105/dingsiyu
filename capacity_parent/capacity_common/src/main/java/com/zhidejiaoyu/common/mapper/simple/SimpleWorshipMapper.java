package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.Worship;
import com.zhidejiaoyu.common.pojo.WorshipExample;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface SimpleWorshipMapper {
    int countByExample(WorshipExample example);

    int deleteByExample(WorshipExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Worship record);

    int insertSelective(Worship record);

    List<Worship> selectByExample(WorshipExample example);

    Worship selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Worship record, @Param("example") WorshipExample example);

    int updateByExample(@Param("record") Worship record, @Param("example") WorshipExample example);

    int updateByPrimaryKeySelective(Worship record);

    int updateByPrimaryKey(Worship record);

    /**
     * 查询用户一周的膜拜信息
     *
     * @param student
     * @return
     */
    List<Worship> selectSevenDaysInfoByStudent(@Param("student") Student student);

    /**
     * 查询上一个被膜拜的最高次数
     * @return
     */
    @Select("select DISTINCT count(w.id) from worship w,student s where w.student_id_by_worship = s.id and s.worship_first_time is not null GROUP BY s.id ")
    Integer countLastFirstCount();

    @MapKey("id")
    Map<Long, Map<String, Long>> getMapKeyStudentWorship();

    /**
     * 本周我被膜拜的次数
     *
     * @param student
     * @param startTime 当前周的开始日期
     * @param endTime   当前周的结束日期
     * @return
     */
    @Select("select count(id) from worship where student_id_by_worship = #{student.id} and worship_time <= #{endTime} and worship_time >= #{startTime}")
    int countByWorshipedThisWeed(@Param("student") Student student, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 查询学生的膜拜记录
     *
     * @param student
     * @param type    1：被膜拜记录；2：膜拜别人记录
     * @return
     */
    List<Map<String, String>> selectStudentNameAndTime(@Param("student") Student student, @Param("type") Integer type);

    /**
     * 查看本周当前学生膜拜指定学生的次数
     *
     * @param currentStudent 当前学生
     * @param student        指定学生
     * @return
     */
    int countWorshipStudentThisWeek(@Param("currentStudent") Student currentStudent, @Param("student") Student student);

    // 添加排行假数据
    @Insert("INSERT INTO ccie (	student_id,	student_name,	unit_id,	get_time,	test_type,	study_model,	ccie_no,	encourage_word,	read_flag)VALUES (#{id},'唐氏',2732,'2018-09-30 11:46:58','7','13','N201809301000','名列前茅',1)")
	void insertInfo(String id);

	void insertList(List<Integer> list);

	void insertListMB(List<Integer> list);

	void insertListXZ(List<Map<String, Object>> list);

	@Update("update award set can_get = 1, get_flag = 1 WHERE type = 3 AND id in (select id from award where student_id = #{id} AND type = 3 ORDER BY id asc limit ${number})")
	void updateXZ(@Param("id") String id, @Param("number") int number);

    /**
     * 统计各个学生被膜拜的次数
     *
     * @return
     */
    @MapKey("studentId")
    Map<Long, Map<Long, Long>> countWorshipWithStudent();

    @Select("select count(id) from worship where student_id_by_worship =#{studentId} and state=2")
    Integer getNumberByStudent(Long id);

    @Update("update worship set state=1 where student_id_by_worship =#{studentId} and state=2")
    Integer updState(Long studentId);

    @MapKey("studentId")
    Map<Long, Map<String, Object>> selCountWorshipByStudents(@Param("list") List<Long> studentIds);

    /**
     * 统计指定学生被膜拜的次数
     *
     * @param studentId
     * @return
     */
    int countByWorship(@Param("studentId") Long studentId);
}



