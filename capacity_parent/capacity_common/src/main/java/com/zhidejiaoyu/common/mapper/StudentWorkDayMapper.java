package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.StudentWorkDay;
import com.zhidejiaoyu.common.pojo.StudentWorkDayExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentWorkDayMapper {
    int countByExample(StudentWorkDayExample example);

    int deleteByExample(StudentWorkDayExample example);

    int deleteByPrimaryKey(Long id);

    int insert(StudentWorkDay record);

    int insertSelective(StudentWorkDay record);

    List<StudentWorkDay> selectByExample(StudentWorkDayExample example);

    StudentWorkDay selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") StudentWorkDay record, @Param("example") StudentWorkDayExample example);

    int updateByExample(@Param("record") StudentWorkDay record, @Param("example") StudentWorkDayExample example);

    int updateByPrimaryKeySelective(StudentWorkDay record);

    int updateByPrimaryKey(StudentWorkDay record);

    /**
     * 获取学生前七个工作日的起始时间
     *
     * @param studentId
     * @return
     */
    StudentWorkDay selectPreviousWorkDay(@Param("studentId") Long studentId);

    /**
     * 查询所有结束时间小于当前时间的学生工作日信息
     *
     * @return
     */
    List<StudentWorkDay> selectEndTimeLessThanNow();

    /**
     * 获取当前时间7个工作日后的时间
     *
     * @return
     */
    String selectAfterSevenDay();

    /**
     * 批量更新
     *
     * @param days
     */
    void updateList(@Param("days") List<StudentWorkDay> days);

    /**
     * 批量增加
     *
     * @param days
     */
    void insertList(@Param("days") List<StudentWorkDay> days);
}