package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentRestudy;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 学生复习记录表，记录学生复习数据 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-01-14
 */
@Repository
public interface StudentRestudyMapper extends BaseMapper<StudentRestudy> {

}