package com.dfdz.teacher.common.log.factory;

import com.zhidejiaoyu.common.mapper.OperationLogMapper;
import com.zhidejiaoyu.common.pojo.OperationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.TimerTask;

/**
 * 日志操作任务创建工厂
 *
 * @author zdjy
 * @date 2016年12月6日 下午9:18:27
 */
@Component
@Slf4j
public class LogTaskFactory {

    private static OperationLogMapper operationLogMapperStatic;

    @Resource
    private OperationLogMapper operationLogMapper;

    @PostConstruct
    public void init() {
        operationLogMapperStatic = this.operationLogMapper;
    }

    public static TimerTask businessLog(final Integer userId, final String businessName, final String clazzName, final String msg) {
        return new TimerTask() {
            @Override
            public void run() {
                OperationLog operationLog = LogFactory.createOperationLog(userId, businessName, clazzName, msg);
                try {
                    operationLogMapperStatic.insert(operationLog);
                } catch (Exception e) {
                    log.error("创建业务日志异常!", e);
                }
            }
        };
    }
}

