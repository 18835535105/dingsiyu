package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.zhidejiaoyu.common.pojo.Teks;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
