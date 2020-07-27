package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.PayLog;
import com.zhidejiaoyu.common.pojo.PayLogExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PayLogMapper extends BaseMapper<PayLog> {
    int countByExample(PayLogExample example);

    int deleteByExample(PayLogExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(PayLog record);

    List<PayLog> selectByExample(PayLogExample example);

    PayLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PayLog record, @Param("example") PayLogExample example);

    int updateByExample(@Param("record") PayLog record, @Param("example") PayLogExample example);

    int updateByPrimaryKeySelective(PayLog record);

    int updateByPrimaryKey(PayLog record);

    List<Map> selectPayLogDataByStudentId(@Param("studentId") long studentId);

    /**
     * 统计学生充值次数
     *
     * @param studentId
     * @return
     */
    @Select("select count(id) from pay_log where student_id = #{studentId}")
    int countByStudent(Long studentId);

    /**
     * 查询学生首次充值时间
     *
     * @param studentId
     * @return
     */
    @Select("SELECT recharge from pay_log where student_id = #{studentId} order by recharge asc limit 1")
    Date selectFirstPayTimeByStudentId(Long studentId);
}
