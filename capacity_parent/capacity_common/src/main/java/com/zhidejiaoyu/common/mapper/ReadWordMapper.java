package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.ReadWord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 查询学生单钱课程的生词
     *
     * @param studentId
     * @param courseId
     * @return
     */
    List<ReadWord> selectByStudentIdCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 获取学生当前课程下的生词数据
     *
     * @param studentId
     * @param courseId
     * @param allWords  在当前单词中获取生词
     * @return
     */
    List<String> selectNeedMarkRedWords(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("allWords") List<String> allWords);
}
