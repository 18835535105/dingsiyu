package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Player;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 播放机学习记录表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-12-15
 */
public interface PlayerMapper extends BaseMapper<Player> {

    /**
     * 查看当前单元下指定类型的播放机学习记录
     *
     * @param studentId
     * @param unitId
     * @param type
     * @param wordId
     * @return
     */
    @Select("select count(id) from player where student_id = #{studentId} and unit_id = #{unitId} and type = #{type} and word_id = #{wordId}")
    int selectByType(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") Integer type,
                     @Param("wordId") Long wordId);

    /**
     * 获取当前单元单词本中单词播放机/句型播放机已学习的个数
     *
     * @param studentId
     * @param unitId
     * @param type      2：单词本中播放机学习记录；3：句型本中播放机学习记录
     * @return
     */
    int countLearnedWord(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") Integer type);

    /**
     * 删除指定记录
     *
     * @param studentId
     * @param unitId
     * @param type      2：单词本中播放机学习记录；3：句型本中播放机学习记录
     */
    void deleteRecord(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") int type);

    /**
     * 获取当前单元学习的次数
     *
     * @param studentId
     * @param unitId
     * @param type  2：单词本中播放机学习记录；3：句型本中播放机学习记录
     * @return
     */
    int selectMaxLearnCount(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") int type);

    Player selectPlayerByType(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") Integer type,
                              @Param("wordId") Long wordId);
}
