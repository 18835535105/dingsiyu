package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Ccie;
import com.zhidejiaoyu.common.pojo.CcieExample;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface CcieMapper extends BaseMapper<Ccie> {
    int countByExample(CcieExample example);

    int deleteByExample(CcieExample example);

    int deleteByPrimaryKey(Long id);

    int insertSelective(Ccie record);

    List<Ccie> selectByExample(CcieExample example);

    Ccie selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Ccie record, @Param("example") CcieExample example);

    int updateByExample(@Param("record") Ccie record, @Param("example") CcieExample example);

    int updateByPrimaryKeySelective(Ccie record);

    int updateByPrimaryKey(Ccie record);

    /**
     * 根据条件查询全部证书信息
     *
     * @param studentId
     * @param model
     * @return
     */
	List<Map<String, Object>> selectAllCcieByStudentIdAndDate(@Param("studentId")Long studentId, @Param("model")Integer model);

    /**
     * 获取当天指定类型最大证书编号
     *
     * @param type
     * @param today 当天时间的 yyyyMMdd
     * @return
     */
    @Select("select ccie_no from ccie where ccie_no like concat(concat('%', #{today}), '%') and test_type = #{type} order by id desc limit 1")
    String selectMaxCcieNo(@Param("type") Integer type, @Param("today") String today);

    /**
     * 获取学生最新获取的证书
     *
     * @param student
     * @param unitId
     * @return
     */
    List<Ccie> selectLastCcie(@Param("student") Serializable student, @Param("unitId") Long unitId);

    int getCountCcieByStudentId(@Param("studentId") Long studentId);

    /**
     * 所有学生对应证书
     * @return
     */
    @MapKey("id")
    Map<Long,Map<String,Long>> getMapKeyStudentCCie();
}