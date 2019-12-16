package com.zhidejiaoyu.student.business.service.simple.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.simple.SimpleGradeMapper;
import com.zhidejiaoyu.common.pojo.Grade;
import com.zhidejiaoyu.student.business.service.simple.SimpleGradeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 班级表 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2018-09-06
 */
@Service
public class SimpleGradeServiceImpl extends ServiceImpl<SimpleGradeMapper, Grade> implements SimpleGradeService {

}
