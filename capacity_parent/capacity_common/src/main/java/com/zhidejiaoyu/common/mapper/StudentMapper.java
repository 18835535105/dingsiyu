package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExample;
import com.zhidejiaoyu.common.vo.smallapp.studyinfo.DailyStateVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface StudentMapper extends BaseMapper<Student> {
    /**
     * @param example
     * @return
     * @see com.baomidou.mybatisplus.core.mapper.BaseMapper#selectList(Wrapper)
     */
    @Deprecated
    List<Student> selectByExample(StudentExample example);

    /**
     * @param id
     * @return
     * @see com.baomidou.mybatisplus.core.mapper.BaseMapper#selectById(Serializable)
     */
    @Deprecated
    Student selectByPrimaryKey(Long id);

    /**
     * @param record
     * @return
     * @see com.baomidou.mybatisplus.core.mapper.BaseMapper#updateById(Object)
     */
    @Deprecated
    int updateByPrimaryKeySelective(Student record);

    /**
     * @param record
     * @return
     * @see com.baomidou.mybatisplus.core.mapper.BaseMapper#updateById(Object)
     */
    @Deprecated
    int updateByPrimaryKey(Student record);

    /**
     * 去重获取所有学校名称
     *
     * @param isNewStudentSchool 是否是新生成账号所属的学校
     *                           <code>true</code> 只查询新生成的账号中的学校名称
     *                           <code>false</code> 查询所有学校名称
     * @return
     */
    List<String> getSchools(@Param("isNewStudentSchool") Boolean isNewStudentSchool);

    /**
     * 验证学生身份
     *
     * @param st
     * @return
     */
    Student loginJudge(Student st);

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
     * @param teacherId
     * @param classId
     * @return
     */
    List<Map<String, Object>> selectSeniority(@Param("model") String model, @Param("teacherId") Long teacherId, @Param("classId") Long classId);

    @MapKey("id")
    Map<Long, Map<Long, Object>> selectxz();

    Map<String, Object> getCourseIdAndUnitId(@Param("studentId") long studentId);

    @Select("select system_gold from student where id = #{studentId}")
    Integer getSystem_gold(Long studentId);

    @Update("update student set system_gold = #{i} where id = #{studentId}")
    int updateBySystem_gold(@Param("i") int i, @Param("studentId") Long studentId);

    @Update("update student set system_gold = #{gold} where id = #{studentId}")
    int updateBySystemGold(@Param("gold") double gold, @Param("studentId") Long studentId);

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

    @Select("select count(id) from student where teacher_id = #{teacherId} and version = #{version}")
    Integer schoolHeadcount(@Param("teacherId") Long teacherId, @Param("version") String version);

    Integer schoolHeadcountNationwide(@Param("study_paragraph") String study_paragraph, @Param("version") String version);

    /**
     * 获取本校所有总金币数为 maxGold 的学生信息
     *
     * @param schoolAdminId
     * @param maxGold
     * @return
     */
    List<Student> selectMaxGoldForGold(@Param("schoolAdminId") Integer schoolAdminId, @Param("maxGold") double maxGold);

    /**
     * 将学生第一次获取全校第一名标识置为null
     *
     * @param notGetModalStudents
     * @return
     */
    int updateSchoolGoldFirstTimeToNull(@Param("students") List<Student> notGetModalStudents);

    @Select("select DATE_FORMAT(register_date,'%Y') from student where id = #{studentId}")
    int getYear(@Param("studentId") long studentId);

    Map getStudentAccountTime(@Param("studentId") long studentId);

    @Update("update student set account_time = #{format} where id = #{studentId}")
    int updateAccountTimeByStudentId(@Param("studentId") long studentId, @Param("format") String format);

    /**
     * 查询指定学生的头像
     *
     * @param studentIds
     * @return
     */
    @MapKey("id")
    Map<Long, Map<Long, String>> selectHeadUrlMapByStudentId(@Param("studentIds") List<Long> studentIds);

    Integer getVocabularyCountByStudent(@Param("studentId") Long studentId);

    Integer getSentenceCountByStudent(@Param("studentId") Long studentId);

    /**
     * 查询校管下已登陆过系统的所有学生个数
     *
     * @param schoolAdminId
     * @return
     */
    int countHasLoginLogStudentsBySchoolAdminId(@Param("schoolAdminId") Integer schoolAdminId);

    /**
     * 获取学校金币最多的学生信息
     *
     * @param schoolAdminId
     * @return
     */
    Student selectMaxGoldForSchool(@Param("schoolAdminId") Integer schoolAdminId);

    /**
     * 查询需要参加排行的学生信息（未删除且登陆过系统）
     *
     * @return
     */
    List<Student> selectHasRank();

    /**
     * 查看所有冻结用户
     *
     * @return
     */
    List<Student> getAllFrozenStudent();

    /**
     * 查询当前学校所有学生信息
     *
     * @param schoolAdminId 校管 id
     * @return
     */
    List<Student> selectBySchoolAdminId(@Param("schoolAdminId") Integer schoolAdminId);

    /**
     * 查询教师下所有学生
     *
     * @param teacherId
     * @return
     */
    List<Student> selectByTeacherId(@Param("teacherId") Integer teacherId);

    /**
     * 查看到期体验账号id
     *
     * @param studentIds
     */
    List<Student> selectDeleteAccount(@Param("studentIds") List<Long> studentIds);

    void deleteByIds(@Param("studentIds") List<Long> studentIds);

    /**
     * 查询到期的体验账号
     *
     * @return
     */
    List<Student> selectExperienceAccount();

    /**
     * 修改学生状态至已删除
     *
     * @param students
     */
    void updateStatus(@Param("students") List<Student> students);

    List<Long> selectAllStudentId();

    List<Map<String, Object>> getAllStudentIdTeacherId();

    /**
     * 根据账号查询学生信息
     *
     * @param account
     * @return
     */
    Student selectByAccount(@Param("account") String account);

    /**
     * 根据openid查询学生信息
     *
     * @param openid
     * @return
     */
    Student selectByOpenId(@Param("openid") String openid);

    /**
     * 查询学生源分战力排行数据
     *
     * @param studentIds
     * @return
     */
    @MapKey("studentId")
    Map<Long, Map<String, Object>> selectSourcePowerRankByIds(@Param("studentIds") List<Long> studentIds);

    /**
     * 查询学生id
     *
     * @param accountArr
     * @return
     */
    List<DailyStateVO> selectByAccounts(@Param("accountArr") String[] accountArr);
}
