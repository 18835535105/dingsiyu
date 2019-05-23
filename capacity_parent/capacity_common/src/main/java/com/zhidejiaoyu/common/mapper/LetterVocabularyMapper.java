package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.LetterUnit;
import com.zhidejiaoyu.common.pojo.LetterVocabulary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-05-17
 */
public interface LetterVocabularyMapper extends BaseMapper<LetterVocabulary> {

    List<LetterVocabulary> selByUnitIds(@Param("major") String major, @Param("subordinate") String subordinate, @Param("unit") Integer unit);

    List<String> selLetterByUnitId(@Param("major") String major, @Param("subordinate") String subordinate, @Param("unit") Integer unit);
}
