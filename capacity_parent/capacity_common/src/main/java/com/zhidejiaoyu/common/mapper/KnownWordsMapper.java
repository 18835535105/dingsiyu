package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.KnownWords;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 记录学生的熟词信息，用于每周活动统计学生熟词数量 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-05-28
 */
public interface KnownWordsMapper extends BaseMapper<KnownWords> {

    /**
     * 查询学生的熟词数
     *
     * @param studentId
     * @return
     */
    @Select("select count(id) from known_words where student_id = #{studentId}")
    int countByStudentId(Long studentId);
}
