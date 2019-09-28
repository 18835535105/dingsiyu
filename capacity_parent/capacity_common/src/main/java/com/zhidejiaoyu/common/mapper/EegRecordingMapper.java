package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.EegRecording;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-09-27
 */
public interface EegRecordingMapper extends BaseMapper<EegRecording> {

    EegRecording selNowByStudent(@Param("studentId") Long studentId);

    EegRecording selRoleStudent(@Param("type") Integer type,@Param("studentId") Long id);
}
