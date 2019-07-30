package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Letter;
import com.zhidejiaoyu.common.pojo.LetterWrite;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-05-20
 */
public interface LetterWriteMapper extends BaseMapper<LetterWrite> {

    LetterWrite selByLetterIdAndStudent(@Param("letterId") Integer letterId,@Param("studentId") Long studentId) ;

    List<Long> selStudyLetterIdByUnitIdAndStudent(@Param("unitId") Long unitId, @Param("studentId") Long studentId);

    Integer selStudyLetterCountByUnitIdAndStudent(@Param("unitId") Long unitId,@Param("studentId") Long studentId);

    @Delete("delete from letter_write where unit_id=#{unitId} and student_id=#{studentId}")
    void delByUnitIdAndStudentId(@Param("unitId") Long unitId,@Param("studentId") Long studentId);

    Map<String,Object> selByLetterMemoryStrengthAndStudent(@Param("letterId") Integer letterId, @Param("unitId") Long unitId, @Param("studentId") Long studentId);

    Integer selByNewWords(@Param("unitId") Long unitId,@Param("studentId") Long studentId);

    Integer selByRipeWords(Long unitId, Long studentId);

    Integer selByToReview(Long unitId, Long studentId);

}
