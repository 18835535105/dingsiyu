package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.Vo.simple.testVo.TestRecordVo;
import com.zhidejiaoyu.common.pojo.TestRecordInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-10-20
 */
public interface SimpleTestRecordInfoMapper extends BaseMapper<TestRecordInfo> {

    /**
     * 清除学生指定单元的测试详情记录
     *
     * @param studentId
     * @param unitId
     */
    @Delete("delete tsi from test_record_info tsi, test_record tr where tsi.test_id = tr.id and tr.student_id = #{studentId} and tr.unit_id = #{unitId}")
    void deleteByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);

    /**
     * 批量插入测试详情
     *
     * @param testRecordInfos
     */
    void insertList(@Param("testRecordInfos") List<TestRecordInfo> testRecordInfos);

    /**
     * 查询当前测试记录对应的测试详情个数
     *
     * @param records
     * @return  key:测试id  value: recordCount 测试详情数
     */
    @MapKey("testId")
    Map<Long, Map<Long, Long>> countByRecordIds(@Param("records") List<TestRecordVo> records);
}