package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Player;
import com.baomidou.mybatisplus.mapper.BaseMapper;
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
    int selectByType(@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("type") Integer type, @Param("wordId") Long wordId);
}
