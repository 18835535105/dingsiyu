package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.LetterUnit;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字母，音节，字母宝典关联的单元表
 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-05-17
 */
public interface LetterUnitMapper extends BaseMapper<LetterUnit> {

    List<LetterUnit> selSymbolUnit(@Param("startUnit") Long startunit,@Param("endUnit") Long endunit);
}
