package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.Vo.study.MemoryStudyVo;
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
    List<String> selectNeedMarkRedWords(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("allWords") List<String> allWords, @Param("readTypeId") Long readTypeId);

    /**
     * 获取达到黄金记忆点的数据
     *
     * @param studentId
     * @param courseId
     * @param type      强化类型：1.慧记忆;2.单词图鉴3.慧听写4.慧默写
     * @return
     */
    MemoryStudyVo selectNeedReview(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("type") Integer type);

    /**
     * 需要复习的单词个数
     *
     * @param studentId
     * @param courseId
     * @param type      强化类型：1.慧记忆;2.单词图鉴3.慧听写4.慧默写
     * @return
     */
    Long countNeedReview(Long studentId, Long courseId, Integer type);

    /**
     * 获取指定的生词手册
     *
     * @param studentId
     * @param courseId
     * @param wordId
     * @param type      强化类型：1.慧记忆;2.单词图鉴3.慧听写4.慧默写
     * @return
     */
    ReadWord selectByStudentIdAndCourseIdAndWordId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("wordId") Long wordId, @Param("type") Integer type);
}
