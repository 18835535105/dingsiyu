package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.RankList;
import com.zhidejiaoyu.common.pojo.RankListExample;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RankListMapper extends BaseMapper<RankList> {
    int countByExample(RankListExample example);

    int deleteByExample(RankListExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(RankList record);

    List<RankList> selectByExample(RankListExample example);

    RankList selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") RankList record, @Param("example") RankListExample example);

    int updateByExample(@Param("record") RankList record, @Param("example") RankListExample example);

    int updateByPrimaryKeySelective(RankList record);

    int updateByPrimaryKey(RankList record);

    /**
     * 批量增加学生排行信息
     *
     * @param rankLists
     * @return
     */
    int insertList(@Param("rankLists") List<RankList> rankLists);

    /**
     * 查询所有学生的排行榜信息
     *
     * @return
     */
    @MapKey("studentId")
    Map<Long, RankList> selectRankListMap();

    /**
     * 批量更新排行榜
     *
     * @param updateList
     * @return
     */
    int updateList(@Param("updateList") List<RankList> updateList);

    /**
     * 查询学生排行数据
     *
     * @param studentId
     * @return
     */
    RankList selectByStudentId(@Param("studentId") Long studentId);
}
