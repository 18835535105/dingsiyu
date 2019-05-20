package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.LetterPair;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 字母配对记忆表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-05-20
 */
public interface LetterPairMapper extends BaseMapper<LetterPair> {

    Integer selCountByUnitAndStudent(@Param("unitId") Long unitId,@Param("studentId") Long studentId);

    List<Long> selAllStudyLetter(@Param("unitId") Long unitId, @Param("studentId") Long studentId);

    Integer selCountStudyLetter(@Param("unitId") Long unitId,@Param("studentId") Long studentId);

    @Delete("delete from letter_pair where unit_id=#{unitId} and student_id=#{studentId}")
    void deleteByUnitAndStudent(@Param("unitId") Long unitId,@Param("studentId") Long studentId);
}
