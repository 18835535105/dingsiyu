package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.common.mapper.RunLogMapper;
import com.zhidejiaoyu.common.pojo.RunLog;
import com.zhidejiaoyu.common.pojo.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * @author wuchenxi
 * @date 2019-8-1 14:23
 */
@Slf4j
@Component
public class SaveRunLog {

    @Autowired
    private RunLogMapper runLogMapper;

    @Autowired
    private ExecutorService executorService;

    /**
     * 保存运行日志
     *
     * @param student
     * @param type
     * @param courseId
     * @param unitId
     * @param msg
     */
    public void saveRunLog(Student student, Integer type, Long courseId, Long unitId, String msg) throws RuntimeException {
        executorService.execute(() -> {
            RunLog runLog = this.packageRunLog(student, type, courseId, unitId, msg);
            runLogMapper.insert(runLog);
        });
    }

    /**
     * 保存运行日志
     *
     * @param student
     * @param type
     * @param msg
     */
    public void saveRunLog(Student student, Integer type, String msg) throws RuntimeException {
        this.saveRunLog(student, type, null, null, msg);
    }

    private RunLog packageRunLog(Student student, Integer type, Long courseId, Long unitId, String msg) {
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
