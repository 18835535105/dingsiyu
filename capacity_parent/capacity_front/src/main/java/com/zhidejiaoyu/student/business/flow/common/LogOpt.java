package com.zhidejiaoyu.student.business.flow.common;

import com.zhidejiaoyu.common.mapper.OpenUnitLogMapper;
import com.zhidejiaoyu.common.pojo.OpenUnitLog;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.UnitNew;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 保存及打印日志
 *
 * @author: wuchenxi
 * @date: 2020/1/3 10:37:37
 */
@Slf4j
@Component
public class LogOpt {

    @Resource
    private OpenUnitLogMapper openUnitLogMapper;

    /**
     * 保存单元开启日志
     *
     * @param student
     * @param unitId
     */
    public void saveOpenUnitLog(Student student, Long unitId) {
        // 保存开启单元日志记录
        OpenUnitLog openUnitLog = new OpenUnitLog();
        openUnitLog.setCreateTime(new Date());
        openUnitLog.setCurrentUnitId(unitId);
        openUnitLog.setStudentId(student.getId());
        try {
            openUnitLogMapper.insert(openUnitLog);
        } catch (Exception e) {
            log.error("学生 {}-{} 保存开启单元日志出错；当前单元 {}，下一单元 {}", student.getId(), student.getStudentName(), unitId, e);
        }
    }

    /**
     * 当需要调过单词图鉴节点的时候记录日志
     *
     * @param student
     * @param model   学习模块
     */
    public void changeFlowNodeLog(Student student, String model, UnitNew unit, int flowId) {
        log.info("单元[{}]没有单词图片，学生[{} - {} - {}]进入{}流程，流程 id=[{}]",
                unit.getJointName(), student.getId(), student.getAccount(), student.getStudentName(), model, flowId);
    }
}
