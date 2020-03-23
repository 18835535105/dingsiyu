package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Teks;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课文表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2018-12-10
 */
public interface TeksMapper extends BaseMapper<Teks> {
    List<Teks> selTeksByUnitId(Integer unitId);

    /**
     * 获取学生课程下所有单元信息
     *
     * @param studentId
     * @param courseIds
     * @return
     */
    List<Map<String, Object>> selectUnitIdAndNameByCourseIds(@Param("studentId") Long studentId, @Param("courseIds") List<Long> courseIds);

    List<Map<String, Object>> selHistoryPronunciation(Map<String,Object> maps);

    List<Map<String, Object>> selHistoryByCountAndUnitId(Map<String, Object> maps);

    Integer isHistoryPronunciation(Map<String, Object> maps);

    List<Map<String,Object>> getStudentAllCourse(@Param("studentId") Long studentId, @Param("courseIds")  List<Long> courseIds);

    List<Teks> getTwentyTeks();

    List<Map<String,Object>> selTeksByCorseId(@Param("courseId") Long courseId);

    Long selTeksBySentence(String word);
}
