package com.dfdz.teacher.common.log.factory;

import com.dfdz.teacher.common.log.LogManager;
import com.zhidejiaoyu.common.pojo.OperationLog;

import java.util.Date;

/**
 * 日志对象创建工厂
 *
 * @author zdjy
 * @date 2016年12月6日 下午9:18:27
 */
public class LogFactory {

    /**
     * 创建操作日志
     */
    public static OperationLog createOperationLog(Integer userId, String businessName, String clazzName, String msg) {
        OperationLog operationLog = new OperationLog();
        operationLog.setLogtype("业务日志");
        operationLog.setLogname(businessName);
        operationLog.setUserid(userId);
        operationLog.setClassname(clazzName);
        operationLog.setMethod(clazzName);
        operationLog.setCreatetime(new Date());
        operationLog.setSucceed("成功");
        operationLog.setMessage(msg);
        return operationLog;
    }

    /**
     * 保存业务日志
     *
     * @param logName
     * @param msg
     */
    public static void saveLog(Integer userId, String logName, String msg) {
        String className = Thread.currentThread().getStackTrace()[2].getClassName();
        LogManager.me().executeLog(LogTaskFactory.businessLog(userId, logName, className, msg));
    }
}
