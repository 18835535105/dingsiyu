package com.zhidejiaoyu.common.mapper.simple;


import com.zhidejiaoyu.common.pojo.News;
import com.zhidejiaoyu.common.pojo.NewsExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface SimpleNewsMapper {
    int countByExample(NewsExample example);

    int deleteByExample(NewsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(News record);

    int insertSelective(News record);

    List<News> selectByExample(NewsExample example);

    News selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") News record, @Param("example") NewsExample example);

    int updateByExample(@Param("record") News record, @Param("example") NewsExample example);

    int updateByPrimaryKeySelective(News record);

    int updateByPrimaryKey(News record);

    /**
     * 批量更新消息记录
     *
     * @param updateList
     * @return
     */
    int updateByList(@Param("updateList") List<News> updateList);

    /**
     * 批量新增消息
     *
     * @param insertList
     * @return
     */
    int insertList(@Param("list") List<News> insertList);

    /**
     * 根据学生id标记为已读
     */
    @Update("update news set read = 1 where studentId = #{studentId}")
    void updateByRead(@Param("studentId") Long studentId);

    /**
     * 根据学生id集合查找其账号到期消息
     *
     * @param stuIds
     * @return
     */
    List<News> selectByStuIds(@Param("list") List<Long> stuIds);
}
