package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.Grade;
import com.zhidejiaoyu.common.pojo.SysUser;

/**
 * <p>
 * 班级表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-09-06
 */
public interface GradeMapper extends BaseMapper<Grade> {

    Grade selectByTeacherId(Long teacherId);
}
