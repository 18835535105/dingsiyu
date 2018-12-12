package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Teacher;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 教师相关信息 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2018-12-11
 */
public interface TeacherMapper extends BaseMapper<Teacher> {

    /**
     * 根据教师id查询教师信息
     *
     * @param teacherId
     * @return
     */
    Teacher selectByTeacherId(Long teacherId);
}
