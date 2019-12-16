package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolSummary;
import com.zhidejiaoyu.common.pojo.RunLog;
import com.zhidejiaoyu.common.pojo.RunLogExample;
import com.zhidejiaoyu.common.pojo.Student;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RunLogMapper extends BaseMapper<RunLog> {
    int countByExample(RunLogExample example);

    int deleteByExample(RunLogExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(RunLog record);

    List<RunLog> selectByExampleWithBLOBs(RunLogExample example);

    List<RunLog> selectByExample(RunLogExample example);

    RunLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") RunLog record, @Param("example") RunLogExample example);

    int updateByExampleWithBLOBs(@Param("record") RunLog record, @Param("example") RunLogExample example);

    int updateByExample(@Param("record") RunLog record, @Param("example") RunLogExample example);

    int updateByPrimaryKeySelective(RunLog record);

    int updateByPrimaryKeyWithBLOBs(RunLog record);

    int updateByPrimaryKey(RunLog record);

    /**
     * 查询当前学生登录总次数
     *
     * @param stuId
     * @return
     */
    @Select("SELECT count(id) FROM run_log WHERE type = 1 and operate_user_id = #{stuId}")
    Integer selectLoginCountByStudentId(@Param("stuId") Long stuId);

    @Select("select log_content from run_log WHERE operate_user_id = #{studentId} AND date_format(create_time, '%Y-%m-%d') = #{formatYYYYMMDD} and type = 4")
    List<String> getStudentGold(@Param("formatYYYYMMDD") String formatYYYYMMDD, @Param("studentId") long studentId);

    /**
     * 根据操作人id查询今日金币获取日志信息
     *
     * @param stuId
     * @return
     */
    List<RunLog> selectTodayLogsByOperateUserId(@Param("stuId") Long stuId);

    /**
     * 获取当前学生指定模块下本次登录学习指定单词/例句个数获取的金币奖励数
     *
     * @param stuId   当前学生id
     * @param loginTime 本次登录时间
     * @param key   1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写
     * @param str   区分奖励类型
     * @return
     */
    int countAwardCount(@Param("stuId") long stuId, @Param("loginTime") String loginTime, @Param("key") Integer key, @Param("str") String str);

    /**
     * 查询当前学生今日登录次数
     *
     * @param stu
     * @return
     */
    int countStudentTodayLogin(@Param("stu") Student stu);

    /**
     * 获取学生当前单元下的奖励日志
     *
     * @param studentId
     * @param unitId
     * @param type 4:金币奖励；7：勋章奖励
     * @return
     */
    List<RunLog> selectGoldByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") int type);

    int getCountXZByStudentId(@Param("studentId")Long studentId);

    /**
     * 今日排行（根据金币）
     * @param date
     * @param model 1=本班排行 2=本校排行 3=全国排行
     * @return 学生id - 从大倒小顺序
     */
    List<Integer> getAllQueryType(@Param("date") String date, @Param("model") String model, @Param("student") Student student);

    @MapKey("studentId")
    Map<Long, Map<String, Object>> getGoldByStudentId(@Param("date") String data, @Param("model") String model, @Param("student") Student student);

    @MapKey("id")
    Map<Long,Map<String,Long>> getMapKeyStudentrunLog();

    /**
     * 查询学生最后一条登录/退出日志
     *
     * @param operateUserId
     * @return
     */
    RunLog selectLastRunLogByOperateUserId(@Param("operateUserId") Long operateUserId);

    /**
     * 统计各个校区学生登录人数
     *
     * @param date
     * @return
     */
    List<StudentInfoSchoolSummary> selectStudentInfoSchoolSummary(@Param("date") String date);

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIdList);
}
