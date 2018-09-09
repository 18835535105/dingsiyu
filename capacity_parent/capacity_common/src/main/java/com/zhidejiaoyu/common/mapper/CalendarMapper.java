package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Calendar;
import com.zhidejiaoyu.common.pojo.CalendarExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CalendarMapper {
    int countByExample(CalendarExample example);

    int deleteByExample(CalendarExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Calendar record);

    int insertSelective(Calendar record);

    List<Calendar> selectByExample(CalendarExample example);

    Calendar selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Calendar record, @Param("example") CalendarExample example);

    int updateByExample(@Param("record") Calendar record, @Param("example") CalendarExample example);

    int updateByPrimaryKeySelective(Calendar record);

    int updateByPrimaryKey(Calendar record);

    /**
     * 获取7天后的工作日日期
     *
     * @param today
     * @return
     */
    String selectAfterSevenDay(@Param("today") String today);
}