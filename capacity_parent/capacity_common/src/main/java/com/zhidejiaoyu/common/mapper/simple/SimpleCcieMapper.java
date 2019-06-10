package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Ccie;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface SimpleCcieMapper extends BaseMapper<Ccie> {

    /**
     * 根据条件查询全部证书信息
     *
     * @param studentId
     * @param model
     * @return
     */
	List<Map<String, Object>> selectAllCcieByStudentIdAndDate(@Param("studentId") Long studentId, @Param("model") Integer model);

	/**
     * 所有学生对应证书
     * @return
     */
    @MapKey("id")
    Map<Long, Map<String, Long>> getMapKeyStudentCCie();

    /**
     * 获取当天指定类型最大证书编号
     *
     * @param type
     * @param today 当天时间的 yyyyMMdd
     * @return
     */
    String selectMaxCcieNo(@Param("type") Integer type, @Param("today") String today);

    /**
     * 更新证书是否已读状态
     *
     * @param studentId 学生id
     * @param readFlag  0：未读；1：已读
     * @return
     */
    @Update("update ccie set read_flag = #{readFlag} where student_id = #{studentId}")
    int updateReadFlag(@Param("studentId") Long studentId, @Param("readFlag") int readFlag);

    /**
     * 查询学生的证书个数
     *
     * @param studentId
     * @return
     */
    int countByStudentId(@Param("studentId") Long studentId);

    /**
     * 为学生插入指定数量的证书
     *
     * @param studentId
     * @param count
     */
    void insertWithNeedCount(@Param("studentId") Long studentId, @Param("count") int count);

    /**
     * 所有学生对应证书
     * @return
     */
    @MapKey("id")
    Map<Long,Map<String,Object>> selCountCcieByStudents(@Param("list") List<Long> studentIds);
}
