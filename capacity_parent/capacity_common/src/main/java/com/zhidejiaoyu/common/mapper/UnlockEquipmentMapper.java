package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.UnlockEquipment;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 解锁总时常 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-05-11
 */
public interface UnlockEquipmentMapper extends BaseMapper<UnlockEquipment> {

    UnlockEquipment selectByStudentId(@Param("studentId") long studentId);
}
