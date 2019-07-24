package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.ReadWord;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 阅读生词手册 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
public interface ReadWordMapper extends BaseMapper<ReadWord> {

    /**
     * 查询当前单词是不是在指定课程的生词手册中
     *
     * @param studentId
     * @param courseId  课程 id
     * @param wordId    单词 id
     * @return 大于0 说明单词已存在于当前课程的生词手册中；否则不存在
     */
    int countByCourseIdAndWordIdAndNotKnow(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("wordId") Long wordId);
}
