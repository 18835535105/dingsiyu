package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface SimpleRunLogMapper extends BaseMapper<RunLog> {
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
     * @param stuId     当前学生id
     * @param loginTime 本次登录时间
     * @param typeStr   7：单元闯关测试；8：复习测试；9：已学测试；10：熟词测试；11：生词测试；
     *                  12：五维测试；13：任务课程；'14:单词辨音; 15:词组辨音; 16:单词认读; 17:词组认读; 18:词汇考点; 19:句型认读;
     *                  20:语法辨析; 21单词拼写; 22:词组拼写;
     * @param str       区分奖励类型
     * @return
     */
    int countAwardCount(@Param("stuId") long stuId, @Param("loginTime") String loginTime, @Param("typeStr") String typeStr, @Param("str") String str);

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
     * @param type      4:金币奖励；7：勋章奖励
     * @return
     */
    List<RunLog> selectGoldByUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") int type);

    int getCountXZByStudentId(@Param("studentId") Long studentId);

    /**
     * 今日排行（根据金币）
     *
     * @param date
     * @param model 1=本班排行 2=本校排行 3=全国排行
     * @return 学生id - 从大倒小顺序
     */
    List<Integer> getAllQueryType(@Param("date") String date, @Param("model") String model, @Param("student") Student student);

    @MapKey("studentId")
    Map<Long, Map<String, Object>> getGoldByStudentId(@Param("date") String data, @Param("model") String model, @Param("student") Student student);

    @MapKey("id")
    Map<Long, Map<String, Long>> getMapKeyStudentrunLog();

}
