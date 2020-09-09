package com.dfdz.teacher.business.prizeExchangeList.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.StudentExchangePrizeTmpMapper;
import com.zhidejiaoyu.common.pojo.StudentExchangePrizeTmp;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 迁移数据时，用于临时存放学生兑奖记录，然后通过奖品名称将这些记录放到兑奖记录正式表中 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2020-09-09
 */
@Service
public class StudentExchangePrizeTmpServiceImpl extends ServiceImpl<StudentExchangePrizeTmpMapper, StudentExchangePrizeTmp> implements StudentExchangePrizeTmpService {

}
