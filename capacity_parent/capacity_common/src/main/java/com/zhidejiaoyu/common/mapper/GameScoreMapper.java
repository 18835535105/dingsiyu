package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.GameScore;
import com.zhidejiaoyu.common.pojo.GameScoreExample;
import com.zhidejiaoyu.common.pojo.Student;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameScoreMapper {
    int countByExample(GameScoreExample example);

    int deleteByExample(GameScoreExample example);

    int deleteByPrimaryKey(Long id);

    int insert(GameScore record);

    int insertSelective(GameScore record);

    List<GameScore> selectByExample(GameScoreExample example);

    GameScore selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") GameScore record, @Param("example") GameScoreExample example);

    int updateByExample(@Param("record") GameScore record, @Param("example") GameScoreExample example);

    int updateByPrimaryKeySelective(GameScore record);

    int updateByPrimaryKey(GameScore record);

    /**
     * 获取学生进行的最后一个游戏的游戏id
     *
     * @param student
     * @return
     */
    @Select("select game_id from game_score where student_id = #{student.id} order by id desc limit 1")
    Long selectGameNameList(@Param("student") Student student);

    void deleteByStudentIds(@Param("studentIds") List<Long> studentIdList);
}
