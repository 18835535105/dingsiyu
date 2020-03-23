package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Letter;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-05-17
 */
public interface LetterMapper extends BaseMapper<Letter> {

     List<Letter> getAllLetterByUnitId(Integer unitId);

    Integer selLetterCountById(Long unitId);

    List<Letter> getByUnitId(Long unitId);

    Letter getStudyLetter(@Param("unitId") Long unitId,@Param("longs") List<Long> longs);

    List<Letter> getThreeLetter(@Param("letterId") Integer letterId);

    Letter selPushLetterByUnitIdAndStudent(@Param("unitId") Long unitId,@Param("studentId") Long studentId);

    List<Letter> getAllLetter();
}
