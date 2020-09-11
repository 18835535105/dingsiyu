package com.zhidejioayu.center.business.pageerror.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhidejiaoyu.common.pojo.center.PageErrorLog;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.pageerror.service.PageErrorLogService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 * 前端页面错误信息收集表 前端控制器
 * </p>
 *
 * @author zdjy
 * @since 2020-09-11
 */
@RestController
@RequestMapping("/pageErrorLog")
public class PageErrorLogController {

    @Resource
    private PageErrorLogService pageErrorLogService;

    /**
     * 保存错误日志
     *
     * @return
     */
    @PostMapping("/saveLog")
    public ServerResponse<Object> saveLog(PageErrorLog log) {
        log.setErrTime(new Date());

        PageErrorLog pageErrorLog = pageErrorLogService.getOne(new LambdaQueryWrapper<PageErrorLog>()
                .eq(PageErrorLog::getAppName, log.getAppName())
                .eq(PageErrorLog::getUserInfo, log.getUserInfo())
                .eq(PageErrorLog::getErrMsg, log.getErrMsg())
                .likeRight(PageErrorLog::getErrTime, DateUtil.formatDate(new Date(), DateUtil.YYYYMMDD)));

        if (pageErrorLog == null) {
            pageErrorLogService.save(log);
        } else {
            pageErrorLog.setErrTime(new Date());
            pageErrorLogService.updateById(pageErrorLog);
        }

        return ServerResponse.createBySuccess();
    }
}

