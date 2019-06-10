package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.Vo.simple.StudentGauntletVo;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExample;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SimpleStudentMapper extends BaseMapper<Student> {
    List<Student> selectByExample(StudentExample example);

    Student selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Student record);

    int updateByPrimaryKey(Student record);

    /**
     * 查询最大id对应的记录
     *
     * @return
     */
    @Select("select * from student where id = (select max(id) from student)")
    Student selectStudentByMaxId();

    /**
     * 批量增加学生信息
     *
     * @param students
     */
    int insertStudentList(List<Student> students);

    /**
     * 去重获取所有学校名称
     *
     * @param isNewStudentSchool 是否是新生成账号所属的学校
     *                           <code>true</code> 只查询新生成的账号中的学校名称
     *                           <code>false</code> 查询所有学校名称
     * @return
     */
    List<String> getSchools(@Param("isNewStudentSchool") Boolean isNewStudentSchool);

    Student LoginJudge(Student st);

    @Select("select role from student where id = #{id}")
    Integer judgeUser(@Param("id") Long id);

    /**
     * 根据学生id数据批量修改有效期和到期时间
     *
     * @param idArr       学生id数组
     * @param rank        有效期
     * @param accountTime 到期时间
     */
    int updateRankAndAccountTimeByIds(@Param("idArr") Long[] idArr, @Param("rank") Integer rank, @Param("accountTime") Date accountTime);


    /**
     * 查询所有学生的年级跟id,过滤掉已过期、未完善必填信息的学生
     *
     * @return
     */
    List<Student> selectIdAndGradeAndVersion();

    /**
     * 批量修改学生信息
     *
     * @param students
     * @return
     */
    int updateByPrimarykeys(@Param("students") List<Student> students);

    @Update("update student set password = #{password} where id = #{id} and account = #{account}")
    Integer updatePassword(@Param("account") String account, @Param("password") String password, @Param("id") Long id);

    /**
     * 查询有效期等于3天的学生
     *
     * @return
     */
    List<Student> selectAccountTimeLessThreeDays();

    Student indexData(Long student_id);

    /**
     * 获取学生排名
     * <p>如果当前班级或者学校或者全国只有一名学生，返回字符串"null"</p>
     *
     * @param student
     * @param flag          1:班级排名；2：学校排名；3：全国排名
     * @param schoolAdminId 校管 id，学校排行使用
     * @return map key:学生id value:map key:学生id value:rank double类型 排名
     */
    @MapKey("id")
    Map<Long, Map<String, Object>> selectLevelByStuId(@Param("student") Student student, @Param("flag") int flag, @Param("schoolAdminId") Integer schoolAdminId);

    @Select("SELECT unit_id from student where id = #{student_id}")
    Integer selectUnit_id(@Param("student_id") Long student_id);

    /**
     * 例句unit
     *
     * @param student_id
     * @return
     */
    @Select("SELECT sentence_unit_id from student where id = #{student_id}")
    Integer selectSentenceUnit_id(Long student_id);

    /**
     * 查询当前这些学生的排名
     *
     * @param students 学生id集合
     * @return
     */
    @MapKey("id")
    Map<Long, Map<Long, String>> selectRankByStudentIds(@Param("students") List<Student> students);

    /**
     * 学生的信息, 证书, 膜拜
     *
     * @param model
     * @param schoolAdminId
     * @return
     */
    List<Map<String, Object>> selectSeniority(@Param("model") String model, @Param("teacherId") Long teacherId, @Param("classId") Long classId, @Param("schoolAdminId") Integer schoolAdminId);

    @MapKey("id")
    Map<Long, Map<Long, Object>> selectxz();

    @Select("select (offline_gold+system_gold)AS jb from student where id = #{id} ")
    Double myGold(@Param("id") Long id);

    @Select("select count(*) AS mb from worship where student_id_by_worship = #{id} ")
    int myMb(@Param("id") Long id);

    @Update("update student set unit_name = #{amendName} where unit_id = #{unitId}")
    Integer updateByUnitNameAndWord(@Param("unitId") Integer unitId, @Param("amendName") String amendName);

    @Update("update student set sentence_unit_name = #{amendName} where sentence_unit_id = #{unitId}")
    void updateByUnitNameAndSentence(Integer unitId, String amendName);

    Map<String, Object> getCourseIdAndUnitId(@Param("studentId") long studentId);

    @Select("select system_gold from student where id = #{studentId}")
    Integer getSystem_gold(Long studentId);

    @Update("update student set system_gold = #{i} where id = #{studentId}")
    int updateBySystem_gold(@Param("i") int i, @Param("studentId") Long studentId);

    @Update("update student set unit_id = #{unitId} where id = #{studentId}")
    void updateUnitId(@Param("studentId") long studentId, @Param("unitId") int unitId);

    @Update("update student set sentence_unit_id = #{unitId} where id = #{studentId}")
    void updatesentenceUnitId(@Param("studentId") long studentId, @Param("unitId") int unitId);

    /**
     * 批量删除学生信息
     *
     * @param ids 学生id集合
     */
    void deleteByPrimaryKeys(@Param("ids") Long[] ids);

    /**
     * 查询已有学习记录的学生账号和姓名
     *
     * @param ids
     * @return
     */
    List<Map<String, String>> selectHasStudyRecord(@Param("ids") Long[] ids);

    /**
     * 批量更新学生删除状态为开启状态
     *
     * @param stuIds 学生id集合
     */
    void updateDelStatus(@Param("stuIds") List<Long> stuIds);

    /**
     * 当前学段学习当前版本的所有学生个数
     *
     * @param phase
     * @param student
     * @return
     */
    int countByPhaseAndVersion(@Param("phase") String phase, @Param("student") Student student);

    @Select("select count(id) from student where area = #{area} and school_name = #{school_name} and version = #{version}")
    Integer schoolHeadcount(@Param("area") String area, @Param("school_name") String school_name, @Param("version") String version);

    Integer schoolHeadcountNationwide(@Param("study_paragraph") String study_paragraph, @Param("version") String version);

    /**
     * 获取本校所有总金币数为 maxGold 的学生信息
     *
     * @param schoolName
     * @param maxGold
     * @return
     */
    List<Student> selectMaxGoldForGold(@Param("schoolName") String schoolName, @Param("maxGold") double maxGold);

    /**
     * 将学生第一次获取全校第一名标识置为null
     *
     * @param notGetModalStudents
     * @return
     */
    int updateSchoolGoldFirstTimeToNull(@Param("students") List<Student> notGetModalStudents);


    /**
     * 查询当前学校金币数最高的学生信息
     *
     * @param schoolName
     * @return
     */
    Student selectMaxGoldForSchool(@Param("schoolName") String schoolName);

    @Select("select DATE_FORMAT(register_date,'%Y') from student where id = #{studentId}")
    int getYear(@Param("studentId") long studentId);

    Map getStudentAccountTime(@Param("studentId") long studentId);


    @Update("update student set account_time = #{format} where id = #{studentId}")
    int updateAccountTimeByStudentId(@Param("studentId") long studentId, @Param("format") String format);

    /**
     * 学生课程个个单元已学单词数量
     *
     * @param studentId 学生id
     * @param courseId  课程id
     * @return
     */
    @MapKey("id")
    Map<Long, Map<Long, Object>> learnUnitsWordSum(@Param("studentId") long studentId, @Param("courseId") long courseId);

    @Select("select system_gold + offline_gold from student where id = #{studentId}")
    double selectTotalGold(@Param("studentId") Long studentId);


    Integer updEnergyByAll();

    @Select("select teacher.school_admin_id from student student join teacher teacher on student.teacher_id = teacher.teacher_id where student.id=#{studentId}")
    Long getSysAdminId(Long studentId);

    List<Student> selCountry();

    List<Student> selWorship();

    List<Student> selWorshipByClassAndSchoolAndCountry(@Param("classId") Long classId, @Param("schoolName") String schoolName,
                                                       @Param("teacherId") Long teacherId, @Param("isClass") boolean isClass);

    List<Student> selWorSchoolByAminId(@Param("adminId") Integer adminId, @Param("teacherIds") List<Integer> teacherIds);

    List<Student> selGoldRankByClassAndSchoolName(@Param("classId") Long classId, @Param("isClassId") boolean schoolName, @Param("teacherId") Long teacherId);

    List<Student> selSchoolRankByAdminId(@Param("adminId") Integer adminId, @Param("teacherIds") List<Integer> teacherIds);

    Integer selSchoolAdminId(Long studentId);

    /**
     * 各个课程下最大的单元 id
     *
     * @param courseIds
     * @param studentId
     * @return
     */
    @MapKey("courseId")
    Map<Long, Map<Long, Long>> selectMaxUnitIdMapByCourseIds(@Param("courseIds") List<Long> courseIds, @Param("studentId") Long studentId);

    /**
     * 查询已登录过系统的学生集合
     *
     * @return
     */
    List<Student> selectStudentList();

    /**
     * 查询各个学生对应的校管 id
     *
     * @param students
     * @return
     */
    @MapKey("studentId")
    Map<Long, Map<Long, Integer>> selectStudentSchoolAdminMap(@Param("students") List<Student> students);

    Map<String, Object> getClassNameAndGoldAndMbAnd(Long id);

    Integer selStudentNumberById(@Param("classId") Long classId, @Param("teacherId") Long teacherId,
                                 @Param("model") String model, @Param("adminId") Integer adminId,
                                 @Param("teacherIds") List<Integer> teacherIds, @Param("account") String account);

    List<Map<String, Object>> getRanking(@Param("classId") Long classId, @Param("teacherId") Long teacherId
            , @Param("gold") String gold, @Param("worship") String worShip
            , @Param("model") String model, @Param("adminId") Integer adminId, @Param("start") Integer start, @Param("end") Integer end, @Param("teacherIds") List<Integer> teacherIds);

    List<Map<String, Object>> getCcieRanking(@Param("classId") Long classId, @Param("teacherId") Long teacherId,
                                             @Param("model") String model, @Param("adminId") Integer adminId,
                                             @Param("start") Integer start, @Param("end") Integer end, @Param("teacherIds") List<Integer> teacherIds);

    List<Map<String, Object>> getMedalRanking(@Param("classId") Long classId, @Param("teacherId") Long teacherId, @Param("model") String model, @Param("adminId") Integer adminId, @Param("start") Integer start, @Param("end") Integer end, @Param("teacherIds") List<Integer> teacherIds);

    /**
     * 查询指定教师下所有学生并按金币总数量倒序排列
     *
     * @param teacherId
     * @return
     */
    List<Student> selectListByTeacherId(@Param("teacherId") int teacherId);

    List<StudentGauntletVo> getClassOrSchoolStudents(@Param("classId") Long classId,
                                                     @Param("teacherId") Long teacherId, @Param("teacherIds") List<Integer> teachers,
                                                     @Param("adminId") Long schoolAdminId, @Param("type") Integer type,
                                                     @Param("start") Integer start, @Param("end") Integer end, @Param("account") String account,
                                                     @Param("studentId") Long studentId);

    List<Student> getAll();

    Integer selNumberById(@Param("classId") Long classId, @Param("teacherId") Long teacherId,
                          @Param("model") String model, @Param("adminId") Integer adminId,
                          @Param("teacherIds") List<Integer> teacherIds, @Param("account") String account,
                          @Param("studentId") Long studentId);

    /**
     * 查询校管下已登陆过系统的所有学生个数
     *
     * @param schoolAdminId
     * @return
     */
    int countHasLoginLogStudentsBySchoolAdminId(@Param("schoolAdminId") Integer schoolAdminId);
}
