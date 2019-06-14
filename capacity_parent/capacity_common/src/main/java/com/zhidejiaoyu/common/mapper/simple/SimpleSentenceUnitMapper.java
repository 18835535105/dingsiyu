package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.SentenceUnit;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 单元表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-29
 */
public interface SimpleSentenceUnitMapper extends BaseMapper<SentenceUnit> {

    /**
     * 根据拼接名查询单元个数
     *
     * @param jointName
     * @return
     */
    @Select("select count(id) from sentence_unit where joint_name = #{jointName}")
    int countByJointName(String jointName);

    /**
     * 根据连接名查询单元信息
     *
     * @param jointName
     * @return
     */
    SentenceUnit selectByJointName(@Param("jointName") String jointName);
}
