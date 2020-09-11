package com.zhidejioayu.center.business.pageerror.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhidejiaoyu.common.mapper.center.PageErrorLogMapper;
import com.zhidejiaoyu.common.pojo.center.PageErrorLog;
import com.zhidejioayu.center.business.pageerror.service.PageErrorLogService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 前端页面错误信息收集表 服务实现类
 * </p>
 *
 * @author zdjy
 * @since 2020-09-11
 */
@Service
public class PageErrorLogServiceImpl extends ServiceImpl<PageErrorLogMapper, PageErrorLog> implements PageErrorLogService {

}
