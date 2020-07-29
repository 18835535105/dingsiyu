package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.CanCreateStudentCount;
import org.apache.ibatis.annotations.Param;

/**
 * @author: wuchenxi
 * @date: 2020/7/16 10:39:39
 */
public interface CanCreateStudentCountMapper extends BaseMapper<CanCreateStudentCount> {

    /**
     * 根据校管id查询校区下可生成临时账号总数量
     *
     * @param id
     * @return
     */
    CanCreateStudentCount selectBySchoolAdminId(@Param("id") Integer id);
}
