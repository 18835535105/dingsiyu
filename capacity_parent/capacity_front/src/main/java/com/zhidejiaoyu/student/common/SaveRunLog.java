package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.RunLogMapper;
import com.zhidejiaoyu.common.pojo.RunLog;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * @author wuchenxi
 * @date 2019-8-1 14:23
 */
@Component
public class SaveRunLog {

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private ExecutorService executorService;

    /**
     * 保存运行日志
     *
     * @param type
     * @param courseId
     * @param unitId
     * @param msg
     */
    public void saveRunLog(Integer type, Long courseId, Long unitId, String msg) {
        executorService.execute(() -> {
            RunLog runLog = this.packageRunLog(type, courseId, unitId, msg);
            runLogMapper.insert(runLog);
        });
    }

    /**
     * 保存运行日志
     *
     * @param type
     * @param msg
     */
    public void saveRunLog(Integer type, String msg) {
        this.saveRunLog(type, null, null, msg);
    }

    private RunLog packageRunLog(Integer type, Long courseId, Long unitId, String msg) {
        HttpSession session = HttpUtil.getHttpSession();
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

        RunLog runLog = new RunLog();
        runLog.setCourseId(courseId);
        runLog.setCreateTime(new Date());
        runLog.setLogContent(msg);
        runLog.setOperateUserId(student.getId());
        runLog.setType(type);
        runLog.setUnitId(unitId);
        return runLog;
    }
}
