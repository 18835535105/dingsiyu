package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.PhoneticSymbol;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-05-17
 */
public interface PhoneticSymbolMapper extends BaseMapper<PhoneticSymbol> {

    List<Map<String,Object>> selByUnitId(Integer id);

    List<PhoneticSymbol> selAllByUnitIdAndSymbol(@Param("unitId") Integer unitId,@Param("symbol") String symbol);

    @Select(" select phonetic_symbol from phonetic_symbol where unit_id=#{unitId} group by phonetic_symbol")
    List<String> selSymbolByUnitId(Integer unitId);

    /**
     * 统计当前单元下音节数量
     *
     * @param unitId
     * @return
     */
    int countByUnitId(@Param("unitId") Long unitId);

    /**
     * 查询当前单元还没有学习的音标信息
     *
     * @param studentId
     * @param studyModel
     * @param unitId
     * @param learns
     * @return
     */
    PhoneticSymbol selectNotLearnByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("studyModel") String studyModel, @Param("unitId") Long unitId, @Param("learns") List<Learn> learns);
}
