package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.ErrorLearnLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-25
 */
public interface ErrorLearnLogMapper extends BaseMapper<ErrorLearnLog> {

    int selectCountByStudentIdAndUnitIdAndEasyOrHard(@Param("studentId") Long studentId, @Param("unitId") long unitId, @Param("easyOrHard") long easyOrHard);

    List<ErrorLearnLog> selectByStudentIdAndUnitIdAndEasyOrHard(@Param("studentId") Long studentId, @Param("unitId") long unitId, @Param("easyOrHard") long easyOrHard);

    List<Map<String, Object>> selectVocabularyByStudentId(@Param("studentId") Long studentId);

    List<Long> selectVocabularyIdByStudentId(@Param("studentId") Long studentId);

    List<ErrorLearnLog> selectVocabularyByStudentIdAndVocabularyIds(@Param("studentId") Long studentId, @Param("vocabularies") List<Long> vocabularyIds);

    Long selectUnitIdByStudentId(@Param("studentId") Long studentId);
}
