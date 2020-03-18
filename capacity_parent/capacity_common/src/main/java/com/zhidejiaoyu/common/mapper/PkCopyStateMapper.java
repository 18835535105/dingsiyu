package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.PkCopyState;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2020-03-17
 */
@Repository
public interface PkCopyStateMapper extends BaseMapper<PkCopyState> {

    PkCopyState selectByStudentIdAndBossId(@Param("studentId") Long id,@Param("bossId") Long bossId);
}